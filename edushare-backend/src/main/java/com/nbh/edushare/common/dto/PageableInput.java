package com.nbh.edushare.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface PageableInput {

    Integer number();
    Integer size();
    String sort();

    default int pageOrDefault() {
        return number() != null && number() >= 0 ? number() : 0;
    }

    default int sizeOrDefault() {
        return size() != null && size() > 0 && size() <= 50 ? size() : 20;
    }

    default Sort sortOrDefault() {
        String sortStr = sort();
        if (sortStr == null || sortStr.isBlank()) {
            return Sort.by("id").descending();
        }

        String[] parts = sortStr.split(",");
        String property = parts[0].trim();
        boolean isDesc = parts.length > 1 && parts[1].trim().equalsIgnoreCase("desc");

        return isDesc ? Sort.by(property).descending() : Sort.by(property).ascending();
    }

    default Pageable toPageable() {
        return PageRequest.of(pageOrDefault(), sizeOrDefault(), sortOrDefault());
    }
}
