package com.msp31.storage1c.domain.dto.response;

import lombok.Value;

import java.util.List;

@Value
public class SearchResult<T> {
    List<T> results;
}
