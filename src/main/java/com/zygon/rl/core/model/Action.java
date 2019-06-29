package com.zygon.rl.core.model;

/**
 * Describes an arbitrary action for the game to interpret.
 *
 */
public class Action {

    // id?
    private final String description;
    private final String displayName;
    private final String name;
    private final String value;

    private Action(Builder builder) {
        this.description = builder.description;
        this.displayName = builder.displayName;
        this.name = builder.name;
        this.value = builder.value;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;
        private String displayName;
        private String description;
        private String value;

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setValue(String value) {
            this.value = value;
            return this;
        }

        public Action build() {
            return new Action(this);
        }
    }
}
