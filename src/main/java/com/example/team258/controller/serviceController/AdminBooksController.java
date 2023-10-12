package com.example.team258.controller.serviceController;

import com.example.team258.dto.AdminBooksRequestDto;
import com.example.team258.dto.MessageDto;
import com.example.team258.security.UserDetailsImpl;
import com.example.team258.service.AdminBooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/admin/books")
@RequiredArgsConstructor
public class AdminBooksController {
    private final AdminBooksService adminBooksService;

    @PostMapping
    public ResponseEntity<MessageDto> createBook(@RequestBody AdminBooksRequestDto requestDto,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return adminBooksService.createBook(requestDto, userDetails.getUser());
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<MessageDto> updateBook(@RequestBody AdminBooksRequestDto requestDto,
                                                 @PathVariable Long bookId,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return adminBooksService.updateBook(requestDto, bookId, userDetails.getUser());
    }
}
