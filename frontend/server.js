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

if (require.main === module) {
  app.listen(port, () => {
    console.log(`Frontend listening on http://localhost:${port}`);
  });
}

module.exports = app;
