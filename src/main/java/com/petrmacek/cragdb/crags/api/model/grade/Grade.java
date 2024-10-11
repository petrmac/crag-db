package com.petrmacek.cragdb.crags.api.model.grade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.petrmacek.cragdb.crags.api.model.GradeSystem;

import java.io.IOException;
import java.util.Optional;

@JsonSerialize(using = Grade.Serializer.class)
@JsonDeserialize(using = Grade.Deserializer.class)
public interface Grade {

    @JsonProperty("value")
    String getValue();

    @JsonIgnore
    int getOrder();

    @JsonProperty("system")
    GradeSystem getSystem();

    default boolean isHigherThan(Grade other) {
        return this.getOrder() > other.getOrder();
    }

    default boolean isLowerThan(Grade other) {
        return this.getOrder() < other.getOrder();
    }

    default Optional<? extends Grade> toSystem(GradeSystem targetSystem) {
        if (targetSystem == this.getSystem()) {
            return Optional.of(this);
        }
        if (targetSystem == GradeSystem.French && this.getSystem() == GradeSystem.UIAA) {
            return ClimbingGradeConverter.convertToFrench((UIAA) this);
        }
        if (targetSystem == GradeSystem.French && this.getSystem() == GradeSystem.YDS) {
            return ClimbingGradeConverter.convertToFrench((YDS) this);
        }
        if (targetSystem == GradeSystem.UIAA && this.getSystem() == GradeSystem.French) {
            return ClimbingGradeConverter.convertToUiaa((French) this);
        }
        if (targetSystem == GradeSystem.YDS && this.getSystem() == GradeSystem.French) {
            return ClimbingGradeConverter.convertToYds((French) this);
        }
        return Optional.empty();
    }


    @JsonCreator
    static Grade forString(String grade, GradeSystem system) {
        return switch (system) {
            case French -> French.forString(grade).orElseThrow();
            case UIAA -> UIAA.forString(grade).orElseThrow();
            case YDS -> YDS.forString(grade).orElseThrow();
        };
    }

    class Serializer extends JsonSerializer<Grade> {

        @Override
        public void serialize(final Grade grade, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("value", grade.getValue());
            jsonGenerator.writeStringField("system", grade.getSystem().getName());
            jsonGenerator.writeEndObject();
        }
    }

    class Deserializer extends StdDeserializer<Grade> {

        public Deserializer() {
            this(null);
        }

        public Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Grade deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            var value = node.get("value");
            var system = node.get("system");
            return Grade.forString(value.asText(), GradeSystem.forString(system.asText()).orElseThrow());
        }
    }
}
