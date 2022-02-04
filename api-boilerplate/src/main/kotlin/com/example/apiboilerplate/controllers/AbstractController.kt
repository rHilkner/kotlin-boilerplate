package com.example.apiboilerplate.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * Created by rodrigohilkner on Nov, 2021
 */
abstract class AbstractController {

    /**
     * Everything this class does is make sure all API responses will be in the following pattern:
     * {
     *   "payload": { ... }
     * }
     * Keeping up the possibility to (in the future) add matadata to the response and have the following pattern:
     * {
     *   "payload": { ... },
     *   "metadata": { ... }
     * }
     * In the boilerplate we'll keep only the "payload", but the app developer may add "metadata" if wanted
     */

    data class ResponsePayload<T>(val payload: T?)

    protected fun <T> response(body: T, httpStatus: HttpStatus): ResponseEntity<ResponsePayload<T>> {
        // Encapsulate responseBody into payload object
        return ResponseEntity(ResponsePayload(body), httpStatus)
    }

    fun response(httpStatus: HttpStatus): ResponseEntity<Any> {
        return ResponseEntity(httpStatus)
    }
}