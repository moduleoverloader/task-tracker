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

const task = {
  id: 1,
  title: 'Walk the dog',
  description: 'Take Maisy to the woods',
  status: 'COMPLETED',
  deadline: '2026-10-29T13:00:00',
};

function renderTaskCard(task) {
  const html = nunjucksEnvironment.renderString(
    "{% from 'macros/task-card.njk' import taskCard %}{{ taskCard(task) }}",
    { task }
  );

  return new JSDOM(html).window.document;
}

describe('task card', () => {
  describe('title', () => {
    it('should set value correctly', () => {
      const taskCard = renderTaskCard(task);
      const cardTitle = taskCard.querySelector('.govuk-summary-card__title').textContent.trim()

      assert.equal(
        cardTitle,
        task.title
      );
    });
  });
  describe('description', () => {
    it('should set value correctly', () => {
      const taskCard = renderTaskCard(task);
      const descriptionRow = [...taskCard.querySelectorAll('.govuk-summary-list__row')]
        .find(row => row.querySelector('dt').textContent.trim() === 'Description');
      const cardDescription = descriptionRow.querySelector('dd').textContent.trim();

      assert.equal(cardDescription, task.description);
    });
  });
  describe('status', () => {
    it('should set value correctly for to do task', () => {
      const taskCard = renderTaskCard({ ...task, status: 'TODO' });
      const cardStatus = taskCard.querySelector('.govuk-tag').textContent.trim()

      assert.equal(
        cardStatus,
        'To do'
      );
    });
    it('should set value correctly for in progress task', () => {
      const taskCard = renderTaskCard({ ...task, status: 'IN_PROGRESS' });
      const cardStatus = taskCard.querySelector('.govuk-tag').textContent.trim()

      assert.equal(
        cardStatus,
        'In progress'
      );
    });
    it('should set value correctly for completed task', () => {
      const taskCard = renderTaskCard({ ...task, status: 'COMPLETED' });
      const cardStatus = taskCard.querySelector('.govuk-tag').textContent.trim()

      assert.equal(
        cardStatus,
        'Completed'
      );
    });
  });
  describe('deadline', () => {
    it('should set value correctly', () => {
      const taskCard = renderTaskCard(task);
      const deadlineRow = [...taskCard.querySelectorAll('.govuk-summary-list__row')]
        .find(row => row.querySelector('dt').textContent.trim() === 'Deadline');
      const cardDeadline = deadlineRow.querySelector('dd').textContent.trim();

      assert.equal(
        cardDeadline,
        '2026-10-29 at 13:00:00'
      );
    });
  });
  describe('status update form', () => {
    describe('status select component', () => {
      it('should assume value matching the task status', () => {
        const taskCard = renderTaskCard(task);
        const statusSelect = taskCard.getElementById(`status-${task.id}`);

        assert.equal(statusSelect.value, task.status);
      });
    });
    describe('update button', () => {
      it('should render with the correct label', () => {
        const taskCard = renderTaskCard(task);
        const updateForm = taskCard.querySelector(
          `form[action="/tasks/${task.id}/status"]`
        );
        const updateButton = updateForm.querySelector('button[type="submit"]');

        assert.equal(updateButton.textContent.trim(), 'Update');
      });
    });
    describe('form submission', () => {
      it('should include the selected status in the update form', () => {
          const taskCard = renderTaskCard(task);
          const form = taskCard.querySelector('form');
          const statusSelect = taskCard.getElementById(`status-${task.id}`);

          assert.equal(form.getAttribute('method'), 'post');
          assert.equal(form.getAttribute('action'), `/tasks/${task.id}/status`);
          assert.equal(statusSelect.name, 'status');

          statusSelect.value = 'TODO';

          const formData = new taskCard.defaultView.FormData(form);
          assert.equal(formData.get('status'), 'TODO');
      });
    });
  });
  describe('delete button', () => {
    it('should be rendered correctly', () => {
      const taskCard = renderTaskCard(task);
      const deleteForm = taskCard.querySelector(
        `form[action="/tasks/${task.id}/delete"]`
      );

      const deleteButtonText = deleteForm.querySelector('button[type="submit"]').textContent.trim();
      assert.equal(
        deleteButtonText,
        'Delete task'
      );
    });
    it('should submit a delete request for the task', () => {
      const taskCard = renderTaskCard(task);
      const deleteForm = taskCard.querySelector(
        `form[action="/tasks/${task.id}/delete"]`
      );

      assert.equal(deleteForm.getAttribute('method'), 'post');
    });
  });
});
