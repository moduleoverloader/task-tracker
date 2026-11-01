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

function renderCreateTaskForm() {
  const html = nunjucksEnvironment.renderString(
    "{% from 'macros/create-task-form.njk' import createTaskForm %}{{ createTaskForm() }}"
  );
  return new JSDOM(html).window.document;
}

describe('create task form', () => {
  describe('title field', () => {
    it('should render', () => {
      const createTaskForm = renderCreateTaskForm();
      const titleField = createTaskForm.querySelector('#task-title');
      const titleLabel = createTaskForm.querySelector('label[for="task-title"]');

      assert.ok(titleField);
      assert.equal(titleLabel.textContent.trim(), 'Title');
    });
    it('should enforce character limit', () => {
      const createTaskForm = renderCreateTaskForm();

      assert.equal(createTaskForm.querySelector('#task-title').getAttribute('maxlength'), '50');
      assert.equal(
        createTaskForm.querySelector('#task-title-count').textContent.trim(),
        'You have 50 characters remaining'
      );
    });
  });
  describe('description field', () => {
    it('should render', () => {
      const createTaskForm = renderCreateTaskForm();
      const descriptionField = createTaskForm.querySelector('#task-description');
      const descriptionLabel = createTaskForm.querySelector('label[for="task-description"]');

      assert.ok(descriptionField);
      assert.equal(descriptionLabel.textContent.trim(), 'Description');
    });
    it('should enforce character limit', () => {
      const createTaskForm = renderCreateTaskForm();

      assert.equal(createTaskForm.querySelector('#task-description').getAttribute('maxlength'), '255');
      assert.equal(
        createTaskForm.querySelector('#task-description-count').textContent.trim(),
        'You have 255 characters remaining'
      );
    });
  });
  describe('deadline field', () => {
    it('should render', () => {
      const createTaskForm = renderCreateTaskForm();
      const deadlineField = createTaskForm.querySelector('#task-deadline');
      const deadlineLabel = createTaskForm.querySelector('label[for="task-deadline"]');

      assert.ok(deadlineField);
      assert.equal(deadlineLabel.textContent.trim(), 'Deadline');
    });
  });
  describe('status field', () => {
    it('should render', () => {
      const createTaskForm = renderCreateTaskForm();
      const statusField = createTaskForm.querySelector('#task-status');
      const statusLabel = createTaskForm.querySelector('label[for="task-status"]');

      assert.ok(statusField);
      assert.equal(statusLabel.textContent.trim(), 'Status');
    });
  });
  describe('create task button', () => {
    it('should render', () => {
      const createTaskForm = renderCreateTaskForm();
      const createTaskButton = createTaskForm.querySelector('button[type="submit"]');
      
      assert.ok(createTaskButton);
      assert.equal(createTaskButton.textContent.trim(), 'Create task');
    });
    it('should submit a create request for the task', () => {
      const createTaskForm = renderCreateTaskForm().querySelector('form');

      assert.equal(createTaskForm.getAttribute('action'), '/tasks');
      assert.equal(createTaskForm.getAttribute('method'), 'post');
    });
  });
});
  