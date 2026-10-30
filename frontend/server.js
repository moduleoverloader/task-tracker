'use strict';

const express = require('express');
const nunjucks = require('nunjucks');
const path = require('node:path');

const app = express();
const port = process.env.PORT || 3000;

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
    const tasksResponse = await fetch('http://localhost:8080/api/tasks');

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
      `http://localhost:8080/api/tasks/${id}/status`,
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
    const response = await fetch(`http://localhost:8080/api/tasks/${id}`, {
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

if (require.main === module) {
  app.listen(port, () => {
    console.log(`Frontend listening on http://localhost:${port}`);
  });
}

module.exports = app;
