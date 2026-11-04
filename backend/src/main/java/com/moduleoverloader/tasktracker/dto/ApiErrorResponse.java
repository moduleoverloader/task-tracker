package com.moduleoverloader.tasktracker.dto;

import java.util.List;

public record ApiErrorResponse(String message, List<ApiFieldError> errors) {
    public record ApiFieldError(String field, String message) {}
}
