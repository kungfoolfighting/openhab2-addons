/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.enocean.internal;

import static org.openhab.binding.enocean.EnOceanBindingConstants.THING_TYPE_ROCKER_SWITCH;

import java.util.Collections;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.enocean.handler.EnOceanHandler;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.polito.elite.enocean.enj.communication.EnJConnection;
import it.polito.elite.enocean.enj.communication.EnJDeviceListener;
import it.polito.elite.enocean.enj.eep.eep26.attributes.EEP26RockerSwitch2RockerAction;
import it.polito.elite.enocean.enj.eep.eep26.attributes.EEP26RockerSwitch2RockerButtonCount;
import it.polito.elite.enocean.enj.link.EnJLink;
import it.polito.elite.enocean.enj.model.EnOceanDevice;

/**
 * The {@link EnOceanHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Jan Kemmler - Initial contribution
 */

@NonNullByDefault
@Component(service = ThingHandlerFactory.class, immediate = true, configurationPid = "binding.enocean")
public class EnOceanHandlerFactory extends BaseThingHandlerFactory implements EnJDeviceListener {

    @SuppressWarnings("null")
    private final Logger logger = LoggerFactory.getLogger(EnOceanHandler.class);

    @SuppressWarnings("null")
    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_ROCKER_SWITCH);
    @Nullable
    private EnJLink linkLayer = null;
    @Nullable
    private EnJConnection connection;

    /**
     * Initializes the {@link EnOceanHandlerFactory}. This function tries to establish a connection to the serial
     * EnOCean device.
     *
     * @param componentContext
     *            component context (must not be null)
     */
    @Override
    protected void activate(ComponentContext componentContext) {
        super.activate(componentContext);
        try {
            String serialPort = (String) componentContext.getProperties().get("serialPort");

            if (null != serialPort) {
                // create the lowest link layer
                linkLayer = new EnJLink(serialPort);

                // create the connection layer
                connection = new EnJConnection(linkLayer, null, this);
                if (null != linkLayer) {
                    // connect the link
                    linkLayer.connect();
                }
            }
        } catch (Exception e) {
            this.logger.debug("The given port does not exist or no device is plugged in. {}", e);
        }
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @SuppressWarnings("null")
    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(THING_TYPE_ROCKER_SWITCH)) {
            if (null != connection) {
                String enoceanAddress = (String) thing.getConfiguration().get("enoceanAddress");
                EnOceanHandler handler = new EnOceanHandler(thing);

                connection.addNewDevice(enoceanAddress, "F6-02-02");
                EnOceanDevice device = connection
                        .getDevice(EnOceanDevice.byteAddressToUID(EnOceanDevice.parseAddress(enoceanAddress)));
                if (null != device) {
                    device.getEEP().addEEP26AttributeListener(0, EEP26RockerSwitch2RockerAction.NAME, handler);
                    device.getEEP().addEEP26AttributeListener(0, EEP26RockerSwitch2RockerButtonCount.NAME, handler);
                } else {
                    return null;
                }
                return handler;
            }
        }

        return null;
    }

    @Override
    public void addedEnOceanDevice(@Nullable EnOceanDevice addedDevice) {
        return;

    }

    @Override
    public void modifiedEnOceanDevice(@Nullable EnOceanDevice changedDevice) {

    }

    @Override
    public void removedEnOceanDevice(@Nullable EnOceanDevice changedDevice) {

    }

}
