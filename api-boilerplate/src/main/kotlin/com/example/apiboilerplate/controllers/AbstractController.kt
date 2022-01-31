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
     * Just keeping up the possibility to (in the future) add matadata to the response and have the following pattern:
     * {
     *   "payload": { ... },
     *   "metadata": { ... }
     * }
     * In the boilerplate we'll keep only the "payload", but the app developer may add "metadata" if wanted
     */

    private data class ResponsePayload(val payload: Any?)

    protected fun response(body: Any?, httpStatus: HttpStatus): ResponseEntity<Any> {
        // Encapsulate responseBody into payload object to differ from (possible future) metadata that we might want to add later in the API
        return ResponseEntity(ResponsePayload(body), httpStatus)
    }
}