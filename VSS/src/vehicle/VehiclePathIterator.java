package vehicle;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * Created by Yang ZHANG on 2014/10/28.
 */
public class VehiclePathIterator implements Iterator {

    private VehiclePath path;

    public VehiclePathIterator(VehiclePath path) {
        this.path = path;
    }

    @Override
    public boolean hasNext() {
        return path != null;
    }

    @Override
    public Object next() {
        if (path == null) {
            throw new NoSuchElementException();
        }
        VehiclePath r = path;
        path = path.getPreviousPath();
        return r;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
