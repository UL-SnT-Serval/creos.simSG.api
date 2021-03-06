package creos.simsg.api.circlefinder;

import creos.simsg.api.model.Configuration;
import creos.simsg.api.model.Fuse;

public interface Circle {
    Fuse[] getFuses();

    /**
     * A circle is effective if all its fuses are closed. Otherwise, it means that there is no circle.
     *
     * @return true if the circle is effective
     */
    boolean isEffective();

    /**
     * Returns true if the circle is effective for the given configuration
     *
     * @param configuration configuration of the circle
     * @return true is the circle is effective for the current configuration, false otherwise
     */
    boolean isEffective(Configuration configuration);

    Fuse getOtherEndPoint(Fuse start);
}
