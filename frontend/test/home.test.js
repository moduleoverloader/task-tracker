'use strict';

const assert = require('node:assert/strict');
const path = require('node:path');
const nunjucks = require('nunjucks');
const { JSDOM } = require('jsdom');
const { describe, it } = require('node:test');

const viewsPath = path.join(__dirname, '../views');
const govukPath = path.join(__dirname, '../node_modules/govuk-frontend/dist');

const nunjucksEnvironment = new nunjucks.Environment(
  new nunjucks.FileSystemLoader([viewsPath, govukPath]),
  { autoescape: true }
);

function renderHome(tasks) {
  const html = nunjucksEnvironment.render('home.njk', { tasks });
  return new JSDOM(html).window.document;
}

describe('home page', () => {
  it('should render one task card for each task', () => {
    const tasks = [
      {
        id: 1,
        title: 'Walk the dog',
        description: 'Take Maisy to the woods',
        status: 'COMPLETED',
        deadline: '2026-10-29T13:00:00',
      },
      {
        id: 2,
        title: 'finish job application',
        description: 'Complete assessment',
        status: 'IN_PROGRESS',
        deadline: '2026-11-05T23:55:00',
      },
    ];

    const document = renderHome(tasks);
    const cards = document.querySelectorAll('.govuk-summary-card');

    assert.equal(cards.length, tasks.length);
  });

  it('should show the empty state message when there are no tasks', () => {
    const document = renderHome([]);

    assert.equal(
      document.querySelector('.govuk-inset-text').textContent.trim(),
      'No tasks yet. Tasks will appear here when they are created.'
    );
    assert.equal(
      document.querySelectorAll('.govuk-summary-card').length,
      0
    );
  });
});
