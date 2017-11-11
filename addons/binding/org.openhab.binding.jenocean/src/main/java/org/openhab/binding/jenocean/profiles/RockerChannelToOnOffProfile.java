package org.openhab.binding.jenocean.profiles;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.profiles.ProfileCallback;
import org.eclipse.smarthome.core.thing.profiles.ProfileTypeUID;
import org.eclipse.smarthome.core.thing.profiles.TriggerProfile;
import org.eclipse.smarthome.core.types.State;

public class RockerChannelToOnOffProfile implements TriggerProfile {

    private final ProfileCallback callback;

    RockerChannelToOnOffProfile(ProfileCallback callback) {
        this.callback = callback;
    }

    @Override
    public ProfileTypeUID getProfileTypeUID() {
        return JEnOceanProfiles.ROCKER_TO_ON_OFF;
    }

    /**
     * Will be called if an item has changed its state and this information should be forwarded to the binding.
     *
     * @param state
     */
    @Override
    public void onStateUpdateFromItem(State state) {

    }

    @Override
    public void onTriggerFromHandler(String event) {
        if (new String("UP_PRESSED").equals(event)) {
            callback.sendCommand(OnOffType.ON);
        } else if (new String("DOWN_PRESSED").equals(event)) {
            callback.sendCommand(OnOffType.OFF);
        }
    }

}
