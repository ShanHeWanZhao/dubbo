/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.remoting.exchange;

import org.apache.dubbo.common.utils.StringUtils;

import java.util.concurrent.atomic.AtomicLong;

import static org.apache.dubbo.common.constants.CommonConstants.HEARTBEAT_EVENT;

/**
 * Request.
 */
public class Request {

    private static final AtomicLong INVOKE_ID = new AtomicLong(0);

    private final long mId;

    private String mVersion;

    /**
     * 标志Request是否需要服务端的响应<p/>
     * 如果为true，则会收到Response <br/>
     * 为false，则不会收到这个Request对应的Response
     */
    private boolean mTwoWay = true;

    /**
     * 标志当前Request是否是一个服务的内部事件（比如心跳检测这种）
     */
    private boolean mEvent = false;

    /**
     * decode这个Request失败后，会设置mBroken为true，代表解码失败，消息格式可能有问题
     */
    private boolean mBroken = false;

    private Object mData;

    public Request() {
        mId = newId();
    }

    public Request(long id) {
        mId = id;
    }

    /**
     * 构建Request的id。<br/>
     * 这个是不需要用分布式id来保持全局唯一的，因为一旦确定了consumer端和provider端，ip和port也就确定了，
     * socket会保证Response的正确返回（返回到对应Request的客户端），对这个客户端来说，id都是唯一的。<br/>
     * 当请求id增加到最大值时（这个是long型），下一次的newId()又回退到最小值，来循环使用id，避免id的用尽。
     * 这肯定是个漫长的过程，所以不可能出现到第二轮了，上一轮的id对应的Request还没有处理掉（因为内部有个超时检测任务，默认1秒内没有收到Response就会超时）
     */
    private static long newId() {
        // getAndIncrement() When it grows to MAX_VALUE, it will grow to MIN_VALUE, and the negative can be used as ID
        return INVOKE_ID.getAndIncrement();
    }

    private static String safeToString(Object data) {
        if (data == null) {
            return null;
        }
        String dataStr;
        try {
            dataStr = data.toString();
        } catch (Throwable e) {
            dataStr = "<Fail toString of " + data.getClass() + ", cause: " +
                    StringUtils.toString(e) + ">";
        }
        return dataStr;
    }

    public long getId() {
        return mId;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public boolean isTwoWay() {
        return mTwoWay;
    }

    public void setTwoWay(boolean twoWay) {
        mTwoWay = twoWay;
    }

    public boolean isEvent() {
        return mEvent;
    }

    public void setEvent(String event) {
        this.mEvent = true;
        this.mData = event;
    }

    public void setEvent(boolean mEvent) {
        this.mEvent = mEvent;
    }

    public boolean isBroken() {
        return mBroken;
    }

    public void setBroken(boolean mBroken) {
        this.mBroken = mBroken;
    }

    public Object getData() {
        return mData;
    }

    public void setData(Object msg) {
        mData = msg;
    }

    public boolean isHeartbeat() {
        return mEvent && HEARTBEAT_EVENT == mData;
    }

    public void setHeartbeat(boolean isHeartbeat) {
        if (isHeartbeat) {
            setEvent(HEARTBEAT_EVENT);
        }
    }

    public Request copy() {
        Request copy = new Request(mId);
        copy.mVersion = this.mVersion;
        copy.mTwoWay = this.mTwoWay;
        copy.mEvent = this.mEvent;
        copy.mBroken = this.mBroken;
        copy.mData = this.mData;
        return copy;
    }

    public Request copyWithoutData() {
        Request copy = new Request(mId);
        copy.mVersion = this.mVersion;
        copy.mTwoWay = this.mTwoWay;
        copy.mEvent = this.mEvent;
        copy.mBroken = this.mBroken;
        return copy;
    }

    @Override
    public String toString() {
        return "Request [id=" + mId + ", version=" + mVersion + ", twoway=" + mTwoWay + ", event=" + mEvent
                + ", broken=" + mBroken + ", data=" + (mData == this ? "this" : safeToString(mData)) + "]";
    }
}
