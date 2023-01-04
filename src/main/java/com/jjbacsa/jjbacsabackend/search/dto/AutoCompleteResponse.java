package com.jjbacsa.jjbacsabackend.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AutoCompleteResponse {
    List<String> autoCompletes;
}
