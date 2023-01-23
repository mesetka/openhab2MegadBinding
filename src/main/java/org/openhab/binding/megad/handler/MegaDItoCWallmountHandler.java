/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.megad.handler;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.megad.MegaDBindingConstants;
import org.openhab.binding.megad.internal.MegaHttpHelpers;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MegaDItoCWallmountHandler} class defines I2C bus feature.
 * You can read I2C sensors connected to one port of MegaD as bus
 *
 * @author Petr Shatsillo - Initial contribution
 */
@NonNullByDefault
public class MegaDItoCWallmountHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(MegaDItoCWallmountHandler.class);
    @Nullable
    MegaDBridgeDeviceHandler bridgeDeviceHandler;
    private @Nullable ScheduledFuture<?> refreshPollingJob;
    protected long lastRefresh = 0;

    /**
     * Creates a new instance of this class for the {@link Thing}.
     *
     * @param thing the thing that should be handled, not null
     */
    public MegaDItoCWallmountHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command.toString().equals("REFRESH")) {
            logger.debug("Refresh request received {}, {}", channelUID, command);
            if (channelUID.getId().equals(MegaDBindingConstants.CHANNEL_I2C_HUM)) {
                assert bridgeDeviceHandler != null;
                String result = "http://" + getBridgeHandler().getThing().getConfiguration().get("hostname").toString()
                        + "/" + getBridgeHandler().getThing().getConfiguration().get("password").toString() + "/?pt="
                        + getThing().getConfiguration().get("port").toString() + "&scl="
                        + getThing().getConfiguration().get("scl").toString() + "&i2c_dev=htu21d";
                String updateRequest = MegaHttpHelpers.sendRequest(result);

                if ("NA".equals(updateRequest)) {
                    logger.debug("Value {} is incorrect for channel {}", updateRequest,
                            MegaDBindingConstants.CHANNEL_I2C_HUM);
                } else {
                    try {
                        updateState(channelUID.getId(), DecimalType.valueOf(updateRequest));
                    } catch (Exception ex) {
                        logger.debug("Value {} is incorrect for channel {}", updateRequest,
                                MegaDBindingConstants.CHANNEL_I2C_HUM);
                    }
                }
            } else if (channelUID.getId().equals(MegaDBindingConstants.CHANNEL_I2C_TEMP)) {
                assert bridgeDeviceHandler != null;
                String result = "http://" + getBridgeHandler().getThing().getConfiguration().get("hostname").toString()
                        + "/" + getBridgeHandler().getThing().getConfiguration().get("password").toString() + "/?pt="
                        + getThing().getConfiguration().get("port").toString() + "&scl="
                        + getThing().getConfiguration().get("scl").toString() + "&i2c_dev=htu21d" + "&i2c_par=1";
                String updateRequest = MegaHttpHelpers.sendRequest(result);

                if ("NA".equals(updateRequest)) {
                    logger.debug("Value {} is incorrect for channel {}", updateRequest,
                            MegaDBindingConstants.CHANNEL_I2C_TEMP);
                } else {
                    try {
                        updateState(channelUID.getId(), DecimalType.valueOf(updateRequest));
                    } catch (Exception ex) {
                        logger.debug("Value {} is incorrect for channel {}", updateRequest,
                                MegaDBindingConstants.CHANNEL_I2C_TEMP);
                    }
                }
            } else if (channelUID.getId().equals(MegaDBindingConstants.CHANNEL_I2C_LIGHTLEVEL)) {
                assert bridgeDeviceHandler != null;
                String result = "http://" + getBridgeHandler().getThing().getConfiguration().get("hostname").toString()
                        + "/" + getBridgeHandler().getThing().getConfiguration().get("password").toString() + "/?pt="
                        + getThing().getConfiguration().get("port").toString() + "&scl="
                        + getThing().getConfiguration().get("scl").toString() + "&i2c_dev=max44009";
                logger.debug("result =  {}", result);
                String updateRequest = MegaHttpHelpers.sendRequest(result);
                logger.debug("updateRequest =  {}", updateRequest);

                if ("NA".equals(updateRequest)) {
                    logger.debug("Value {} is incorrect for channel {}", updateRequest,
                            MegaDBindingConstants.CHANNEL_I2C_LIGHTLEVEL);
                } else {
                    try {
                        updateState(channelUID.getId(), DecimalType.valueOf(updateRequest));
                    } catch (Exception ex) {
                        logger.debug("Value {} is incorrect for channel {}", updateRequest,
                                MegaDBindingConstants.CHANNEL_I2C_LIGHTLEVEL);
                    }
                }
            } else if (channelUID.getId().equals(MegaDBindingConstants.CHANNEL_I2C_CO)) {
                assert bridgeDeviceHandler != null;
                String result = "http://" + getBridgeHandler().getThing().getConfiguration().get("hostname").toString()
                        + "/" + getBridgeHandler().getThing().getConfiguration().get("password").toString() + "/?pt="
                        + getThing().getConfiguration().get("port").toString() + "&scl="
                        + getThing().getConfiguration().get("scl").toString() + "&i2c_dev=t67xx";
                String updateRequest = MegaHttpHelpers.sendRequest(result);

                if ("NA".equals(updateRequest)) {
                    logger.debug("Value {} is incorrect for channel {}", updateRequest,
                            MegaDBindingConstants.CHANNEL_I2C_CO);
                } else {
                    try {
                        updateState(channelUID.getId(), DecimalType.valueOf(updateRequest));
                    } catch (Exception ex) {
                        logger.debug("Value {} is incorrect for channel {}", updateRequest,
                                MegaDBindingConstants.CHANNEL_I2C_CO);
                    }
                }
            }
        }
    }

    @SuppressWarnings("null")
    @Override
    public void initialize() {
        bridgeDeviceHandler = getBridgeHandler();
        logger.debug("Thing Handler for {} started", getThing().getUID().getId());

        String[] rr = { getThing().getConfiguration().get("refresh").toString() };// .split("[.]");
        logger.debug("Thing {}, refresh interval is {} sec", getThing().getUID().toString(), rr[0]);
        float msec = Float.parseFloat(rr[0]);
        int pollingPeriod = (int) (msec * 1000);
        if (refreshPollingJob == null || refreshPollingJob.isCancelled()) {
            refreshPollingJob = scheduler.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    refresh(pollingPeriod);
                }
            }, 0, 100, TimeUnit.MILLISECONDS);
        }
        updateStatus(ThingStatus.ONLINE);
    }

    public void refresh(int interval) {
        long now = System.currentTimeMillis();
        if (interval != 0) {
            if (now >= (lastRefresh + interval)) {
                updateData();
                lastRefresh = now;
            }
        }
    }

    @SuppressWarnings("null")
    protected void updateData() {
        logger.debug("Updating Megadevice thing {}...", getThing().getUID().toString());
        for (Channel channel : getThing().getChannels()) {
            if (isLinked(channel.getUID().getId())) {
                if (channel.getUID().getId().equals(MegaDBindingConstants.CHANNEL_I2C_HUM)) {
                    assert bridgeDeviceHandler != null;
                    String result = "http://"
                            + getBridgeHandler().getThing().getConfiguration().get("hostname").toString() + "/"
                            + getBridgeHandler().getThing().getConfiguration().get("password").toString() + "/?pt="
                            + getThing().getConfiguration().get("port").toString() + "&scl="
                            + getThing().getConfiguration().get("scl").toString() + "&i2c_dev=htu21d";
                    String updateRequest = MegaHttpHelpers.sendRequest(result);

                    if ("NA".equals(updateRequest)) {
                        logger.debug("Value {} is incorrect for channel {}", updateRequest,
                                MegaDBindingConstants.CHANNEL_I2C_HUM);
                    } else {
                        try {
                            updateState(channel.getUID().getId(), DecimalType.valueOf(updateRequest));
                        } catch (Exception ex) {
                            logger.debug("Value {} is incorrect for channel {}", updateRequest,
                                    MegaDBindingConstants.CHANNEL_I2C_HUM);
                        }
                    }
                } else if (channel.getUID().getId().equals(MegaDBindingConstants.CHANNEL_I2C_TEMP)) {
                    assert bridgeDeviceHandler != null;
                    String result = "http://"
                            + getBridgeHandler().getThing().getConfiguration().get("hostname").toString() + "/"
                            + getBridgeHandler().getThing().getConfiguration().get("password").toString() + "/?pt="
                            + getThing().getConfiguration().get("port").toString() + "&scl="
                            + getThing().getConfiguration().get("scl").toString() + "&i2c_dev=htu21d" + "&i2c_par=1";
                    String updateRequest = MegaHttpHelpers.sendRequest(result);

                    if ("NA".equals(updateRequest)) {
                        logger.debug("Value {} is incorrect for channel {}", updateRequest,
                                MegaDBindingConstants.CHANNEL_I2C_TEMP);
                    } else {
                        try {
                            updateState(channel.getUID().getId(), DecimalType.valueOf(updateRequest));
                        } catch (Exception ex) {
                            logger.debug("Value {} is incorrect for channel {}", updateRequest,
                                    MegaDBindingConstants.CHANNEL_I2C_TEMP);
                        }
                    }
                } else if (channel.getUID().getId().equals(MegaDBindingConstants.CHANNEL_I2C_LIGHTLEVEL)) {
                    assert bridgeDeviceHandler != null;
                    String result = "http://"
                            + getBridgeHandler().getThing().getConfiguration().get("hostname").toString() + "/"
                            + getBridgeHandler().getThing().getConfiguration().get("password").toString() + "/?pt="
                            + getThing().getConfiguration().get("port").toString() + "&scl="
                            + getThing().getConfiguration().get("scl").toString() + "&i2c_dev=max44009";
                    String updateRequest = MegaHttpHelpers.sendRequest(result);

                    if ("NA".equals(updateRequest)) {
                        logger.debug("Value {} is incorrect for channel {}", updateRequest,
                                MegaDBindingConstants.CHANNEL_I2C_LIGHTLEVEL);
                    } else {
                        try {
                            updateState(channel.getUID().getId(), DecimalType.valueOf(updateRequest));
                        } catch (Exception ex) {
                            logger.debug("Value {} is incorrect for channel {}", updateRequest,
                                    MegaDBindingConstants.CHANNEL_I2C_LIGHTLEVEL);
                        }
                    }
                } else if (channel.getUID().getId().equals(MegaDBindingConstants.CHANNEL_I2C_CO)) {
                    assert bridgeDeviceHandler != null;
                    String result = "http://"
                            + getBridgeHandler().getThing().getConfiguration().get("hostname").toString() + "/"
                            + getBridgeHandler().getThing().getConfiguration().get("password").toString() + "/?pt="
                            + getThing().getConfiguration().get("port").toString() + "&scl="
                            + getThing().getConfiguration().get("scl").toString() + "&i2c_dev=t67xx";
                    String updateRequest = MegaHttpHelpers.sendRequest(result);

                    if ("NA".equals(updateRequest)) {
                        logger.debug("Value {} is incorrect for channel {}", updateRequest,
                                MegaDBindingConstants.CHANNEL_I2C_CO);
                    } else {
                        try {
                            updateState(channel.getUID().getId(), DecimalType.valueOf(updateRequest));
                        } catch (Exception ex) {
                            logger.debug("Value {} is incorrect for channel {}", updateRequest,
                                    MegaDBindingConstants.CHANNEL_I2C_CO);
                        }
                    }
                }
            }
        }
    }

    // @SuppressWarnings("null")
    // -------------------------------------------------------------------
    private synchronized @Nullable MegaDBridgeDeviceHandler getBridgeHandler() {
        Bridge bridge = Objects.requireNonNull(getBridge());
        return getBridgeHandler(bridge);
    }

    private synchronized @Nullable MegaDBridgeDeviceHandler getBridgeHandler(Bridge bridge) {
        ThingHandler handler = Objects.requireNonNull(bridge.getHandler());
        if (handler instanceof MegaDBridgeDeviceHandler) {
            return (MegaDBridgeDeviceHandler) handler;
        } else {
            logger.debug("No available bridge handler found yet. Bridge: {} .", bridge.getUID());
            return null;
        }
    }

    @SuppressWarnings("null")
    @Override
    public void dispose() {
        if (refreshPollingJob != null && !refreshPollingJob.isCancelled()) {
            refreshPollingJob.cancel(true);
            refreshPollingJob = null;
        }
        super.dispose();
    }
}
