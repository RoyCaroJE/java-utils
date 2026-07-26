package io.github.roycaroje.springreststarter;

import io.github.roycaroje.springrest.advice.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(GlobalExceptionHandler.class)
public class SpringRestAutoConfiguration {

}