/*
 * Copyright (c) 2015 by Gerrit Grunwald
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

package eu.hansolo.enzo.common;

import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;

import java.util.*;


/**
 * User: hansolo
 * Date: 09.11.13
 * Time: 09:08
 */
public class GradientLookup {
    private Map<Double, Stop> stops;


    // ******************** Constructors **************************************
    public GradientLookup () {
        this(new Stop[]{});
    }
    public GradientLookup(final Stop... STOPS) {
        stops = new TreeMap<>();
        Arrays.asList(STOPS).forEach(stop -> stops.put(stop.getOffset(), stop));
        init();
    }


    // ******************** Initialization ************************************
    private void init() {
        if (stops.isEmpty()) return;

        double minFraction = stops.keySet().stream().mapToDouble(offset -> offset).min().getAsDouble();
        double maxFraction = stops.keySet().stream().mapToDouble(offset -> offset).max().getAsDouble();

        if (Double.compare(minFraction, 0) > 0) { stops.put(0.0, new Stop(0.0, stops.get(minFraction).getColor())); }
        if (Double.compare(maxFraction, 1) < 0) { stops.put(1.0, new Stop(1.0, stops.get(maxFraction).getColor())); }
    }


    // ******************** Methods *******************************************
    public Color getColorAt(final double POSITION_OF_COLOR) {
        if (stops.isEmpty()) return Color.BLACK;

        final double POSITION = POSITION_OF_COLOR < 0 ? 0 : (POSITION_OF_COLOR > 1 ? 1 : POSITION_OF_COLOR);
        final Color COLOR;
        if (stops.size() == 1) {
            final Map<Double, Color> ONE_ENTRY = (Map<Double, Color>) stops.entrySet().iterator().next();
            COLOR = stops.get(ONE_ENTRY.keySet().iterator().next()).getColor();
        } else {
            Stop lowerBound = stops.get(0.0);
            Stop upperBound = stops.get(1.0);
            for (Double fraction : stops.keySet()) {
                if (Double.compare(fraction,POSITION) < 0) {
                    lowerBound = stops.get(fraction);
                }
                if (Double.compare(fraction, POSITION) > 0) {
                    upperBound = stops.get(fraction);
                    break;
                }
            }
            COLOR = interpolateColor(lowerBound, upperBound, POSITION);
        }
        return COLOR;
    }

    public List<Stop> getStops() { return new ArrayList<>(stops.values()); }
    public void setStops(final Stop... STOPS) { setStops(Arrays.asList(STOPS)); }
    public void setStops(final List<Stop> STOPS) {
        stops.clear();
        STOPS.forEach(stop -> stops.put(stop.getOffset(), stop));
        init();
    }

    private Color interpolateColor(final Stop LOWER_BOUND, final Stop UPPER_BOUND, final double POSITION) {
        final double POS  = (POSITION - LOWER_BOUND.getOffset()) / (UPPER_BOUND.getOffset() - LOWER_BOUND.getOffset());

        final double DELTA_RED     = (UPPER_BOUND.getColor().getRed()     - LOWER_BOUND.getColor().getRed())     * POS;
        final double DELTA_GREEN   = (UPPER_BOUND.getColor().getGreen()   - LOWER_BOUND.getColor().getGreen())   * POS;
        final double DELTA_BLUE    = (UPPER_BOUND.getColor().getBlue()    - LOWER_BOUND.getColor().getBlue())    * POS;
        final double DELTA_OPACITY = (UPPER_BOUND.getColor().getOpacity() - LOWER_BOUND.getColor().getOpacity()) * POS;

        double red     = LOWER_BOUND.getColor().getRed()     + DELTA_RED;
        double green   = LOWER_BOUND.getColor().getGreen()   + DELTA_GREEN;
        double blue    = LOWER_BOUND.getColor().getBlue()    + DELTA_BLUE;
        double opacity = LOWER_BOUND.getColor().getOpacity() + DELTA_OPACITY;

        red     = red < 0 ? 0     : (red > 1 ? 1     : red);
        green   = green < 0 ? 0   : (green > 1 ? 1   : green);
        blue    = blue < 0 ? 0    : (blue > 1 ? 1    : blue);
        opacity = opacity < 0 ? 0 : (opacity > 1 ? 1 : opacity);

        return Color.color(red, green, blue, opacity);
    }
}
