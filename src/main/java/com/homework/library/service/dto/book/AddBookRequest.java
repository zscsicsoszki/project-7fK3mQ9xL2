package com.homework.library.service.dto.book;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddBookRequest {

    private String title;
    private String author;
}
