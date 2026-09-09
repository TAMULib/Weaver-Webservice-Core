package edu.tamu.weaver.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonInclude;

import tools.jackson.core.StreamReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Jackson 3 Configuration.
 */
@Configuration 
public class JsonMapperConfig {

  /**
   * Default constructor.
   */
  public JsonMapperConfig() {
    // Must exist, even if empty, to comply with standard JavaDocs practices.
  }

  @Bean
  JsonMapper jsonMapper() {
    return JsonMapper
      .builderWithJackson2Defaults()
      .configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .configure(MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES, true)
      .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true)
      .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
      .configure(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION, true)
      .changeDefaultPropertyInclusion(incl -> incl
        .withValueInclusion(JsonInclude.Include.NON_NULL)
        .withContentInclusion(JsonInclude.Include.NON_NULL)
      )
      .findAndAddModules()
      .build();
  }
}
