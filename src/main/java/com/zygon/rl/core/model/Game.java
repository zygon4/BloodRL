package com.zygon.rl.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Game
 *
 */
public final class Game {

    private final Set<Attribute> attributes;
    private final Stack<Context> context;
    private final Date date;
    private final String description;
    private final String displayName;
    private final List<String> log;
    private final String name;
    private final Regions regions;

    private Game(Builder builder) {
        this.attributes = builder.attributes != null
                ? Collections.unmodifiableSet(builder.attributes) : Collections.emptySet();
        this.context = Objects.requireNonNull(builder.context);
        this.date = Objects.requireNonNull(builder.date);
        this.description = builder.description != null ? builder.description : "";
        this.displayName = builder.displayName != null ? builder.displayName : "";
        this.log = builder.log != null
                ? Collections.unmodifiableList(builder.log) : Collections.emptyList();
        this.name = builder.name != null ? builder.name : "";
        this.regions = Objects.requireNonNull(builder.regions);
    }

    public Game add(Attribute attribute) {
        Set<Attribute> attrs = new HashSet<>(attributes);
        attrs.add(attribute);
        return copy().setAttributes(attributes).build();
    }

    public Set<Attribute> getAttributes() {
        return attributes;
    }

    public Context getContext() {
        return context.peek();
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Supplier<Input> getInputSupplier() {
        return getContext().getInputSupplier();
    }

    public List<String> getLog() {
        return log;
    }

    public String getName() {
        return name;
    }

    public Regions getRegions() {
        return regions;
    }

    public Game handleAction(Action action) {
        return getContext().getGameActionProvider().apply(action, this);

        // TBD: who removes ie "pops" contexts when they're done?
    }

    public Action match(Input input) {
        return getContext().getActionProvider().apply(input, this);
    }

    // will find the context with this context's name and overwrite it
    public Game setLeafContext(Context context) {
        // TODO:
        return this;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder copy() {
        return new Builder(this);
    }

    public static class Builder {

        private Set<Attribute> attributes;
        private Stack<Context> context;
        private Date date = new Date(0);
        // Could become a "id" grouping object
        private String description;
        private String displayName;
        private List<String> log;
        private String name;
        private Regions regions;

        private Builder() {
        }

        private Builder(Game game) {
            this.attributes = game.attributes;
            this.context = new Stack<>();
            this.context.addAll(game.context);
            this.date = game.date;
            this.description = game.description;
            this.displayName = game.displayName;
            this.log = new ArrayList<>(game.log);
            this.name = game.name;
            this.regions = game.regions;
        }

        public Builder addContext(Context context) {
            if (this.context == null) {
                this.context = new Stack<>();
            }
            this.context.add(context);
            return this;
        }

        public Builder addLog(String message) {
            if (this.log == null) {
                this.log = new ArrayList<>();
            }
            this.log.add(message);
            return this;
        }

        public Builder removeContext() {
            if (this.context != null) {
                this.context.pop();
            }
            return this;
        }

        public Builder setAttributes(Set<Attribute> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder setContext(Stack<Context> context) {
            this.context = context;
            return this;
        }

        public Builder setDate(Date date) {
            this.date = date;
            return this;
        }

        public Builder moveTime(long duration, TimeUnit timeUnit) {
            this.date.setTime(this.date.getTime() + timeUnit.toMillis(duration));
            return this;
        }

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

        public void setLog(List<String> log) {
            this.log = log;
        }

        public Builder setRegions(Regions regions) {
            this.regions = regions;
            return this;
        }

        public Game build() {
            return new Game(this);
        }
    }
}
