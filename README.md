# task-tracker
DTS Developer Technical Test Submission

---
# Technologies

* Java 21  
* Spring Boot
* Docker
* Spring Data JPA  
* Postgres
* JUnit 5  
* Mockito  

---
# Startup instructions

1. Ensure Docker Desktop is running
2. Navigate to root of project and run `docker compose up --build`

---

# API Endpoints


## GET /api/tasks  
Retrieves all tasks

### Path parameters
No path parameters are required for this endpoint

### Request body

No request body is required for this endpoint

### Response

`200 OK` — Returns a JSON array of tasks. If no tasks exist, the array is empty (`[]`)
```json
[
  {
    "id": 1,
    "title": "Task 1",
    "description": "Description for Task 1",
    "status": "TODO",
    "deadline": "2026-10-30T17:00:00"
  },
  {
    "id": 2,
    "title": "Task 2",
    "description": "Description for Task 2",
    "status": "IN_PROGRESS",
    "deadline": "2026-10-30T17:00:00"
  },
  {
    "id": 3,
    "title": "Task 3",
    "description": "Description for Task 3",
    "status": "COMPLETED",
    "deadline": "2026-10-30T17:00:00"
  }
]
```

### Response fields:

| Field       | Type     | Description                                                                    |
|-------------|----------|--------------------------------------------------------------------------------|
| id          | `Number` | Unique identifier for the task                                                 |
| title       | `String` | Task title                                                                     |
| description | `String` | Task description                                                               |
| status      | `String` | Current task status. Possible values are `TODO`, `IN_PROGRESS` and `COMPLETED` |
| deadline    | `String` | Task deadline, a JSON string in ISO date-time format - `2026-10-30T17:00:00`   |


----

## GET /api/tasks/{id}
Retrieves a specific task by its `id`

### Path Parameters

| Parameter  | Type   | Description                    |
|------------|--------|--------------------------------|
| id         | `Long` | Unique identifier for the task |

### Request body

No request body is required for this endpoint

### Responses

`200 OK` — Returns the task with the specified `id`  
```json
{
  "id": 1,
  "title": "Task 1",
  "description": "Description for Task 1",
  "status": "TODO",
  "deadline": "2026-10-30T17:00:00"
}
```
`404 Not Found` — The task with the specified `id` does not exist


### Response fields

| Field       | Type     | Description                                                                    |
|-------------|----------|--------------------------------------------------------------------------------|
| id          | `Number` | Unique identifier for the task                                                 |
| title       | `String` | Task title                                                                     |
| description | `String` | Task description                                                               |
| status      | `String` | Current task status. Possible values are `TODO`, `IN_PROGRESS` and `COMPLETED` |
| deadline    | `String` | Task deadline, a JSON string in ISO date-time format - `2026-10-30T17:00:00`   |


---

## POST /api/tasks  
Creates a new task

### Path parameters
No path parameters are required for this endpoint

### Request body

Send a JSON object with the following fields:

| Field       | Type     | Required   | Validation                                              |
|-------------|----------|------------|---------------------------------------------------------|
| title       | `String` | Yes        | Must not be null or blank                               |
| description | `String` | No         | Task description                                        |
| status      | `String` | Yes        | Must be `TODO`, `IN_PROGRESS`, or `COMPLETED`           |
| deadline    | `String` | Yes        | ISO date-time format, for example `2026-10-30T17:00:00` |

The deadline is a local date-time and does not include a timezone or UTC offset


### Sample request body
```json
{
    "title": "Task 1",
    "description": "Description for Task 1",
    "status": "TODO",
    "deadline": "2026-10-30T17:00:00"
}
```

### Responses

`201 Created` — The task was created. Returns the created task, including its generated `id`
```json
{
  "id": 1,
  "title": "Task 1",
  "description": "Description for Task 1",
  "status": "TODO",
  "deadline": "2026-10-30T17:00:00"
}
```

`400 Bad Request` - The request is invalid, such as a missing required field, a blank title, an invalid status, or a malformed deadline
```json
{
  "timestamp": "2026-10-27T14:27:39.323Z",
  "status": 400,
  "error": "Bad Request",
  "path": "/api/tasks"
}
```




---

## DELETE /api/tasks/{id}  

Deletes a specific task by its `id`

### Path parameters
| Parameter  | Type     | Description                    |
|------------|----------|--------------------------------|
| id         | `Number` | Unique identifier for the task |

### Request body

No request body is required for this endpoint

### Responses

`204 No Content` — The task was successfully deleted. No response body is returned

`404 Not Found` — The task with the specified `id` does not exist. No response body is returned

---

## PATCH /api/tasks/{id}/status  

Updates the status of a specific task by its `id`

### Path parameters
| Parameter  | Type     | Description                    |
|------------|----------|--------------------------------|
| id         | `Number` | Unique identifier for the task |


### Request body
Send a JSON object with the following field:

| Field       | Type     | Required   | Validation                                              |
|-------------|----------|------------|---------------------------------------------------------|
| status      | `String` | Yes        | Must be `TODO`, `IN_PROGRESS`, or `COMPLETED`           |

Request is sent with a JSON body containing the task details

### Sample request body
```json
{
    "status": "IN_PROGRESS"
}
```

### Responses

`200 OK` — The task status was successfully updated. Returns the updated task object
```json
{
  "id": 1,
  "title": "Task 1",
  "description": "Description for Task 1",
  "status": "IN_PROGRESS",
  "deadline": "2026-10-30T17:00:00"
}
```

`400 Bad Request` - The request is invalid due to an invalid status
```json
{
  "timestamp": "2026-10-27T14:27:39.323Z",
  "status": 400,
  "error": "Bad Request",
  "path": "/api/tasks/1/status"
}
```

`404 Not Found` — The task with the specified `id` does not exist. No response body is returned

