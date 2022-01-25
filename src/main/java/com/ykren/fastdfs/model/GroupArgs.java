package com.ykren.fastdfs.model;

import java.util.Objects;

/**
 * @author ykren
 * @date 2022/1/21
 */
public class GroupArgs extends BaseArgs {
    protected GroupArgs() {
    }

    protected String groupName;

    public String group() {
        return groupName;
    }

    /**
     * Base argument builder class for {@link GroupArgs}.
     */
    public abstract static class Builder<B extends GroupArgs.Builder<B, A>, A extends GroupArgs>
            extends BaseArgs.Builder<B, A> {

        @SuppressWarnings("unchecked")
        public B group(String name) {
            operations.add(args -> args.groupName = name);
            return (B) this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupArgs groupArgs = (GroupArgs) o;
        return Objects.equals(groupName, groupArgs.groupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName);
    }
}
