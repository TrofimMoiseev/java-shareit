package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestClient requestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(USER_ID_HEADER) @Positive long userId,
                                             @Valid @RequestBody RequestDto dto) {
        return requestClient.addNewRequest(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByUserId(@RequestHeader(USER_ID_HEADER) @Positive long userId) {
        return requestClient.getAllRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsByUserIdNot(@RequestHeader(USER_ID_HEADER) @Positive long userId,
                                                            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                            @RequestParam(name = "size", defaultValue = "20") @Positive Integer size) {
        return requestClient.getAllRequestsByUserIdNot(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(USER_ID_HEADER) @Positive long userId,
                                                 @PathVariable @Positive long requestId) {
        return requestClient.getRequestById(userId, requestId);
    }
}
