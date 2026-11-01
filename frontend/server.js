'use strict';

const express = require('express');
const nunjucks = require('nunjucks');
const path = require('node:path');

const app = express();
const port = process.env.PORT || 3000;
const apiBaseUrl = process.env.API_BASE_URL || 'http://localhost:8080';

app.set('view engine', 'njk');

nunjucks.configure(
  [
    path.join(__dirname, 'views'),
    path.join(__dirname, 'node_modules/govuk-frontend/dist'),
  ],
  {
    autoescape: true,
    express: app,
  }
);

app.use(express.static(path.join(__dirname, 'public')));
app.use(express.urlencoded({ extended: false }));

app.use(
  '/assets',
  express.static(
    path.join(__dirname, 'node_modules/govuk-frontend/dist/govuk/assets')
  )
);

app.use(
  '/govuk',
  express.static(
    path.join(__dirname, 'node_modules/govuk-frontend/dist/govuk')
  )
);

app.get('/', async (req, res) => {
  try {
    const tasksResponse = await fetch(`${apiBaseUrl}/api/tasks`);

    if (!tasksResponse.ok) {
      throw new Error(`Backend returned ${tasksResponse.status}`);
    }
    const tasks = await tasksResponse.json();
    res.render('home', { tasks });
  } catch (error) {
    console.error('Could not load tasks:', error);
    res.status(502).send('Could not load tasks from the backend.');
  }
});

app.post('/tasks/:id/status', async (req, res) => {
  const { id } = req.params;
  const { status } = req.body;
  const validStatuses = ['TODO', 'IN_PROGRESS', 'COMPLETED'];

  if (!validStatuses.includes(status)) {
    return res.status(400).send('Invalid task status.');
  }

  try {
    const response = await fetch(
      `${apiBaseUrl}/api/tasks/${id}/status`,
      {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ status }),
      }
    );

    if (!response.ok) {
      return res.status(response.status).send('Could not update task status.');
    }

    res.redirect('/');
  } catch (error) {
    console.error('Could not update task status:', error);
    res.status(502).send('Could not update task status.');
  }
});

app.post('/tasks/:id/delete', async (req, res) => {
  const { id } = req.params;

  try {
    const response = await fetch(`${apiBaseUrl}/api/tasks/${id}`, {
      method: 'DELETE',
    });

    if (response.status === 404) {
      return res.status(404).send('Task not found.');
    }

    if (!response.ok) {
      return res.status(response.status).send('Could not delete task.');
    }

    res.redirect('/');
  } catch (error) {
    console.error('Could not delete task:', error);
    res.status(502).send('Could not delete task.');
  }
});

app.post('/tasks', async (req, res) => {
  const { title, description, status, deadline } = req.body;
  const validStatuses = ['TODO', 'IN_PROGRESS', 'COMPLETED'];

  if (!title?.trim() || !validStatuses.includes(status) || !deadline) {
    return res.status(400).send('Title, status, and deadline are required.');
  }

  if (title.trim().length > 50) {
    return res.status(400).send('Title must be 50 characters or fewer.');
  }

  if (description && description.length > 255) {
    return res.status(400).send('Description must be 255 characters or fewer.');
  }

  try {
    const response = await fetch(`${apiBaseUrl}/api/tasks`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        title: title.trim(),
        description: description || '',
        status,
        deadline,
      }),
    });

    if (!response.ok) {
      return res.status(response.status).send('Could not create task.');
    }

    res.redirect('/');
  } catch (error) {
    console.error('Could not create task:', error);
    res.status(502).send('Could not create task.');
  }
});

if (require.main === module) {
  app.listen(port, () => {
    console.log(`Frontend listening on http://localhost:${port}`);
  });
}

module.exports = app;
