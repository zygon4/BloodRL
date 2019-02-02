/*
 * ========================================================================
 *
 * Copyright (c) by Hitachi Vantara, 2018. All rights reserved.
 *
 * ========================================================================
 */
package com.zygon.rl.core.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Intended to be a possibly nested-chain of context info.
 *
 */
public class Context {

    private final Function<Input, Action> actionProvider;
    private final Set<Attribute> attributes;
    private final BiFunction<Action, Game, Game> gameActionProvider;
    private final Supplier<Input> inputSupplier;
    private final String name;
    // should be attribute?
    private final String displayName;

    private Context(Builder builder) {
        this.actionProvider = Objects.requireNonNull(builder.actionProvider);
        this.attributes = builder.attributes;
        this.gameActionProvider = Objects.requireNonNull(builder.gameActionProvider);
        this.inputSupplier = Objects.requireNonNull(builder.inputSupplier);
        this.name = Objects.requireNonNull(builder.name);
        this.displayName = builder.displayName;
    }

    public Context add(Attribute attribute) {
        Set<Attribute> attrs = new HashSet<>(attributes);
        attrs.add(attribute);
        return copy().setAttributes(attributes).build();
    }

    public Function<Input, Action> getActionProvider() {
        return actionProvider;
    }

    public Set<Attribute> getAttributes() {
        return attributes;
    }

    public Set<Attribute> getAttributes(String name) {
        return attributes.stream()
                .filter(a -> a.getName().equals(name))
                .collect(Collectors.toSet());
    }

    public BiFunction<Action, Game, Game> getGameActionProvider() {
        return gameActionProvider;
    }

    public Supplier<Input> getInputSupplier() {
        return inputSupplier;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder copy() {
        return new Builder(this);
    }

    public static class Builder {

        private Function<Input, Action> actionProvider;
        private Set<Attribute> attributes;
        private BiFunction<Action, Game, Game> gameActionProvider;
        private Supplier<Input> inputSupplier;
        private String name;
        private String displayName;

        private Builder() {
        }

        private Builder(Context context) {
            this.actionProvider = context.getActionProvider();
            this.attributes = context.getAttributes();
            this.gameActionProvider = context.getGameActionProvider();
            this.inputSupplier = context.getInputSupplier();
            this.name = context.getName();
            this.displayName = context.getDisplayName();
        }

        public Builder setActionProvider(Function<Input, Action> actionProvider) {
            this.actionProvider = actionProvider;
            return this;
        }

        public Builder addAttribute(Attribute attribute) {
            if (this.attributes == null) {
                this.attributes = new HashSet<>();
            }
            this.attributes.add(attribute);
            return this;
        }

        public Builder setAttributes(Set<Attribute> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder setGameActionProvider(BiFunction<Action, Game, Game> gameActionProvider) {
            this.gameActionProvider = gameActionProvider;
            return this;
        }

        public Builder setInputSupplier(Supplier<Input> inputSupplier) {
            this.inputSupplier = inputSupplier;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Context build() {
            return new Context(this);
        }
    }
}
