package com.msp31.storage1c.domain.dto.response;

import lombok.Value;

import java.util.List;

@Value
public class FileInfoShort {
    String name;
    String description;
    List<String> tags;
}
