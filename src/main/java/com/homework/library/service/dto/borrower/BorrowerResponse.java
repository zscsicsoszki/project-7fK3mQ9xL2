package com.homework.library.service.dto.borrower;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BorrowerResponse {

    private Long id;
    private String borrower;
}
