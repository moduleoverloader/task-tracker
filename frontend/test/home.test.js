'use strict';

const assert = require('node:assert/strict');
const path = require('node:path');
const nunjucks = require('nunjucks');
const { test } = require('node:test');

const viewsPath = path.join(__dirname, '../views');
const govukPath = path.join(
  __dirname,
  '../node_modules/govuk-frontend/dist'
);

const nunjucksEnvironment = new nunjucks.Environment(
  new nunjucks.FileSystemLoader([viewsPath, govukPath]),
  { autoescape: true }
);

test('renders task data as cards with all visible fields', () => {
  const html = nunjucksEnvironment.render('home.njk', {
    tasks: [
      {
        id: 1,
        title: 'Walk the dog',
        description: 'Take Maisy to the woods',
        status: 'TODO',
        deadline: '2026-10-29T13:00:00',
      },
      {
        id: 2,
        title: 'Finish job application',
        description: 'Finish assessment implementation and write supporting statement',
        status: 'IN_PROGRESS',
        deadline: '2026-11-05T23:55:00',
      },
      {
        id: 3,
        title: 'Fix the sink',
        description: 'The tap is leaking',
        status: 'COMPLETED',
        deadline: '2026-10-31T00:00:00',
      }
    ],
  });

  const cards = [...html.matchAll(
    /<article class='govuk-summary-card'>([\s\S]*?)<\/article>/g
  )].map(match => match[1]);

  const expectedCards = [
    {
      title: 'Walk the dog',
      status: 'To do',
      description: 'Take Maisy to the woods',
      deadline: '2026-10-29 at 13:00:00',
    },
    {
      title: 'Finish job application',
      status: 'In progress',
      description: 'Finish assessment implementation and write supporting statement',
      deadline: '2026-11-05 at 23:55:00',
    },
    {
      title: 'Fix the sink',
      status: 'Completed',
      description: 'The tap is leaking',
      deadline: '2026-10-31 at 00:00:00',
    },
  ];

  assert.equal(cards.length, expectedCards.length);

  for (const [index, card] of cards.entries()) {
    const expected = expectedCards[index];

    assert.ok(card.includes(expected.title));
    assert.ok(card.includes(expected.status));
    assert.ok(card.includes(expected.description));
    assert.ok(card.includes(expected.deadline));
  }
});

test('shows empty tasks list message when there are no tasks', () => {
  const html = nunjucksEnvironment.render('home.njk', { tasks: [] });

  assert.match(html, /No tasks yet/);
});
