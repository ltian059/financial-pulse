package com.fp.content.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "API for Content Management")
@RestController
@RequestMapping("/api/content")
public class ContentController {
}
