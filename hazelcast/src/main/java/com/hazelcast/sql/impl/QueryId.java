/*
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.sql.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.util.UuidUtil;

import java.io.IOException;
import java.util.UUID;

/**
 * Cluster-wide unique query ID.
 */
public class QueryId implements DataSerializable {
    /**
     * Create new query ID for the given member.
     *
     * @param memberId Member ID.
     * @return Query ID.
     */
    public static QueryId create(String memberId) {
        UUID qryId = UuidUtil.newUnsecureUUID();

        return new QueryId(memberId, qryId.getLeastSignificantBits(), qryId.getLeastSignificantBits());
    }

    /** Member ID. */
    private String memberId;

    /** Local ID: most significant bits. */
    private long localHigh;

    /** Local ID: least significant bits. */
    private long localLow;

    public QueryId() {
        // No-op.
    }

    public QueryId(String memberId, long localHigh, long localLow) {
        this.memberId = memberId;
        this.localHigh = localHigh;
        this.localLow = localLow;
    }

    public String getMemberId() {
        return memberId;
    }

    public UUID getLocalId() {
        return new UUID(localHigh, localLow);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(memberId);
        out.writeLong(localHigh);
        out.writeLong(localLow);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        memberId = in.readUTF();
        localHigh = in.readLong();
        localLow = in.readLong();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        QueryId other = (QueryId)o;

        return localHigh == other.localHigh && localLow == other.localLow &&
            memberId != null ? memberId.equals(other.memberId) : other.memberId == null;
    }

    @Override
    public int hashCode() {
        int result = memberId != null ? memberId.hashCode() : 0;

        result = 31 * result + (int) (localHigh ^ (localHigh >>> 32));
        result = 31 * result + (int) (localLow ^ (localLow >>> 32));

        return result;
    }

    @Override
    public String toString() {
        return "QueryId {memberId=" + memberId + ", id=" + getLocalId() + '}';
    }
}
