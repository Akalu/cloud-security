package com.cloud.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {

    @Id
    private Long id;

    @Column(value = "name")
    @JsonProperty("name")
    String name;

    @Column(value = "owner")
    @JsonProperty("owner")
    String owner;

}