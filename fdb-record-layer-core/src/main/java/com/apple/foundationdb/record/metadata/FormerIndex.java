/*
 * FormerIndex.java
 *
 * This source file is part of the FoundationDB open source project
 *
 * Copyright 2015-2018 Apple Inc. and the FoundationDB project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apple.foundationdb.record.metadata;

import com.apple.foundationdb.API;
import com.apple.foundationdb.record.RecordMetaDataProto;
import com.apple.foundationdb.tuple.Tuple;
import com.google.protobuf.ByteString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.apple.foundationdb.record.metadata.Index.decodeSubspaceKey;

/**
 * The location where a deleted index used to live.
 *
 * Nothing is remembered about the index other than this, so that the now-unused range can be deleted when an older record store is upgraded to newer meta-data.
 */
@API(API.Status.MAINTAINED)
public class FormerIndex {
    @Nonnull
    private final Object subspaceKey;
    private final int addedVersion;
    private final int removedVersion;
    @Nullable
    private final String formerName;

    public FormerIndex(@Nonnull Object subspaceKey, int addedVersion, int removedVersion, @Nullable String formerName) {
        this.subspaceKey = subspaceKey;
        this.addedVersion = addedVersion;
        this.removedVersion = removedVersion;
        this.formerName = formerName;
    }

    public FormerIndex(@Nonnull RecordMetaDataProto.FormerIndex proto) {
        this(decodeSubspaceKey(proto.getSubspaceKey()),
                proto.getAddedVersion(), proto.getRemovedVersion(),
                proto.hasFormerName() ? proto.getFormerName() : null);
    }

    /**
     * Get the subspace key formerly occupied by the index.
     *
     * This subspace will be cleared for record stores old enough to have seen the index.
     * @return the index subspace key
     */
    public Object getSubspaceKey() {
        return subspaceKey;
    }

    /**
     * Synonym for {@link #getRemovedVersion}.
     * @return the removed version
     * @deprecated use {@link #getRemovedVersion}
     */
    @Deprecated
    public int getVersion() {
        return addedVersion;
    }

    /**
     * Get the version at which the index was first added.
     * @return the added version
     */
    public int getAddedVersion() {
        return addedVersion;
    }

    /**
     * Get the version at which the index was removed.
     * @return the removed version
     */
    public int getRemovedVersion() {
        return removedVersion;
    }

    /**
     * Get the name under which the index existed, if known.
     * @return the former name
     */
    @Nullable
    public String getFormerName() {
        return formerName;
    }

    @Nonnull
    public RecordMetaDataProto.FormerIndex toProto() {
        RecordMetaDataProto.FormerIndex.Builder builder = RecordMetaDataProto.FormerIndex.newBuilder();
        builder.setSubspaceKey(ByteString.copyFrom(Tuple.from(subspaceKey).pack()));
        if (addedVersion > 0) {
            builder.setAddedVersion(addedVersion);
        }
        if (removedVersion > 0) {
            builder.setRemovedVersion(removedVersion);
        }
        return builder.build();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("FormerIndex {").append(subspaceKey);
        if (formerName != null) {
            str.append("=").append(formerName);
        }
        str.append("}#").append(addedVersion).append("-").append(removedVersion);
        return str.toString();
    }

}
