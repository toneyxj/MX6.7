package com.moxi.bookreader.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by King on 2017/11/1.
 */

public class TestModel implements Serializable {

    /**
     * status : OK
     * origin_addresses : ["Vancouver, BC, Canada","Seattle, état de Washington, états-Unis"]
     * destination_addresses : ["San Francisco, Californie, états-Unis","Victoria, BC, Canada"]
     * rows : [{"elements":[{"status":"OK","duration":{"value":340110,"text":"3 jours 22 heures"},"distance":{"value":1734542,"text":"1 735 km"}},{"status":"OK","duration":{"value":24487,"text":"6 heures 48 minutes"},"distance":{"value":129324,"text":"129 km"}}]},{"elements":[{"status":"OK","duration":{"value":288834,"text":"3 jours 8 heures"},"distance":{"value":1489604,"text":"1 490 km"}},{"status":"OK","duration":{"value":14388,"text":"4 heures 0 minutes"},"distance":{"value":135822,"text":"136 km"}}]}]
     */

    private String status;
    private List<String> origin_addresses;
    private List<String> destination_addresses;
    private List<RowsBean> rows;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getOrigin_addresses() {
        return origin_addresses;
    }

    public void setOrigin_addresses(List<String> origin_addresses) {
        this.origin_addresses = origin_addresses;
    }

    public List<String> getDestination_addresses() {
        return destination_addresses;
    }

    public void setDestination_addresses(List<String> destination_addresses) {
        this.destination_addresses = destination_addresses;
    }

    public List<RowsBean> getRows() {
        return rows;
    }

    public void setRows(List<RowsBean> rows) {
        this.rows = rows;
    }

    public static class RowsBean {
        private List<ElementsBean> elements;

        public List<ElementsBean> getElements() {
            return elements;
        }

        public void setElements(List<ElementsBean> elements) {
            this.elements = elements;
        }

        public static class ElementsBean {
            /**
             * status : OK
             * duration : {"value":340110,"text":"3 jours 22 heures"}
             * distance : {"value":1734542,"text":"1 735 km"}
             */

            private String status;
            private DurationBean duration;
            private DistanceBean distance;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public DurationBean getDuration() {
                return duration;
            }

            public void setDuration(DurationBean duration) {
                this.duration = duration;
            }

            public DistanceBean getDistance() {
                return distance;
            }

            public void setDistance(DistanceBean distance) {
                this.distance = distance;
            }

            public static class DurationBean {
                /**
                 * value : 340110
                 * text : 3 jours 22 heures
                 */

                private int value;
                private String text;

                public int getValue() {
                    return value;
                }

                public void setValue(int value) {
                    this.value = value;
                }

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }
            }

            public static class DistanceBean {
                /**
                 * value : 1734542
                 * text : 1 735 km
                 */

                private int value;
                private String text;

                public int getValue() {
                    return value;
                }

                public void setValue(int value) {
                    this.value = value;
                }

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }
            }
        }
    }
}
