package org.infomedia.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    /**
     * Creates a  ModelMapper bean for mapping between objects.
     *
     * @return a new {@link ModelMapper} instance
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
