package com.ankit.autojunit.sample_project;

import lombok.*;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Customer {
    @Id
    private String id;
    @NonNull
    private String firstName;
    private String lastName;
}
