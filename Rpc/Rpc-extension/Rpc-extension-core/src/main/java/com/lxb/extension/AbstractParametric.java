/**
 *
 */
package com.lxb.extension;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 抽象参数
 */
public abstract class AbstractParametric implements Parametric {

    @Override
    public String getString(final String key) {
        Object target = getObject(key);
        return target == null ? null : target.toString();
    }

    @Override
    public String getString(final String key, final String def) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return def;
        }
        return value;
    }

    @Override
    public String getString(final String key, final String candidate, final String def) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            value = getString(candidate, def);
        }
        return value;
    }

    @Override
    public String getString(final URLOption<String> option) {
        return option == null ? null : getString(option.getName(), option.getValue());
    }

    @Override
    public String getString(final URLBiOption<String> option) {
        return option == null ? null : getString(option.getName(), option.getCandidate(), option.getValue());
    }

    @Override
    public Date getDate(final String key, final Date def) {
        return Converts.getDate(getString(key), def);
    }

    @Override
    public Date getDate(final String key, final SimpleDateFormat format) {
        return Converts.getDate(getString(key), format, null);
    }

    @Override
    public Date getDate(final String key, final SimpleDateFormat format, final Date def) {
        return Converts.getDate(getString(key), format, def);
    }

    @Override
    public Date getDate(final String key, final DateParser parser, final Date def) {
        return Converts.getDate(getObject(key), parser, def);
    }

    @Override
    public Float getFloat(final String key) {
        return Converts.getFloat(getString(key), null);
    }

    @Override
    public Float getFloat(final String key, final Float def) {
        return Converts.getFloat(getString(key), def);
    }

    @Override
    public Float getFloat(final String key, final String candidate, final Float def) {
        Float result = getFloat(key);
        return result != null ? result : getFloat(candidate, def);
    }

    @Override
    public Float getFloat(final URLOption<Float> option) {
        return option == null ? null : getFloat(option.getName(), option.getValue());
    }

    @Override
    public Float getFloat(final URLBiOption<Float> option) {
        return option == null ? null : getFloat(option.getName(), option.getCandidate(), option.getValue());
    }

    @Override
    public Double getDouble(final String key) {
        return Converts.getDouble(getString(key), null);
    }

    @Override
    public Double getDouble(final String key, final String candidate, final Double def) {
        Double result = getDouble(key);
        return result != null ? result : getDouble(candidate, def);
    }

    @Override
    public Double getDouble(final String key, final Double def) {
        return Converts.getDouble(getString(key), def);
    }

    @Override
    public Double getDouble(final URLOption<Double> option) {
        return option == null ? null : getDouble(option.getName(), option.getValue());
    }

    @Override
    public Double getDouble(final URLBiOption<Double> option) {
        if (option == null) {
            return null;
        }
        Double result = getDouble(option.getName());
        if (result == null) {
            result = getDouble(option.getCandidate(), option.getValue());
        }
        return result;
    }

    @Override
    public Long getLong(final String key) {
        return Converts.getLong(getString(key), null);
    }

    @Override
    public Long getLong(final String key, final String candidate, final Long def) {
        Long result = getLong(key);
        return result != null ? result : getLong(candidate, def);
    }

    @Override
    public Long getLong(final String key, final Long def) {
        return Converts.getLong(getString(key), def);
    }

    @Override
    public Long getLong(final URLOption<Long> option) {
        return option == null ? null : getLong(option.getName(), option.getValue());
    }

    @Override
    public Long getLong(final URLBiOption<Long> option) {
        return option == null ? null : getLong(option.getName(), option.getCandidate(), option.getValue());
    }

    @Override
    public Integer getInteger(final String key) {
        return Converts.getInteger(getString(key), null);
    }

    @Override
    public Integer getInteger(final String key, final Integer def) {
        return Converts.getInteger(getString(key), def);
    }

    @Override
    public Integer getInteger(final String key, final String candidate, final Integer def) {
        Integer result = getInteger(key);
        return result != null ? result : getInteger(candidate, def);
    }

    @Override
    public Integer getInteger(final URLOption<Integer> option) {
        return option == null ? null : getInteger(option.getName(), option.getValue());
    }

    @Override
    public Integer getInteger(final URLBiOption<Integer> option) {
        return option == null ? null : getInteger(option.getName(), option.getCandidate(), option.getValue());
    }

    @Override
    public Short getShort(final String key) {
        return Converts.getShort(getString(key), null);
    }

    @Override
    public Short getShort(final String key, final Short def) {
        return Converts.getShort(getString(key), def);
    }

    @Override
    public Short getShort(final String key, final String candidate, final Short def) {
        Short result = getShort(key);
        return result != null ? result : getShort(candidate, def);
    }

    @Override
    public Short getShort(final URLOption<Short> option) {
        return option == null ? null : getShort(option.getName(), option.getValue());
    }

    @Override
    public Short getShort(final URLBiOption<Short> option) {
        return option == null ? null : getShort(option.getName(), option.getCandidate(), option.getValue());
    }

    @Override
    public Byte getByte(final String key) {
        return Converts.getByte(getString(key), null);
    }

    @Override
    public Byte getByte(final String key, final Byte def) {
        return Converts.getByte(getString(key), def);
    }

    @Override
    public Byte getByte(final String key, final String candidate, final Byte def) {
        Byte result = getByte(key);
        return result != null ? result : getByte(candidate, def);
    }

    @Override
    public Byte getByte(final URLOption<Byte> option) {
        return option == null ? null : getByte(option.getName(), option.getValue());
    }

    @Override
    public Byte getByte(final URLBiOption<Byte> option) {
        return option == null ? null : getByte(option.getName(), option.getCandidate(), option.getValue());
    }

    @Override
    public Boolean getBoolean(final String key) {
        return Converts.getBoolean(getString(key), null);
    }

    @Override
    public Boolean getBoolean(final String key, final Boolean def) {
        return Converts.getBoolean(getString(key), def);
    }

    @Override
    public Boolean getBoolean(final String key, final String candidate, final Boolean def) {
        Boolean result = getBoolean(key);
        return result != null ? result : getBoolean(candidate, def);
    }

    @Override
    public Boolean getBoolean(final URLOption<Boolean> option) {
        return option == null ? null : getBoolean(option.getName(), option.getValue());
    }

    @Override
    public Boolean getBoolean(final URLBiOption<Boolean> option) {
        return option == null ? null : getBoolean(option.getName(), option.getCandidate(), option.getValue());
    }

    @Override
    public Long getNatural(final String key, final Long def) {
        return Converts.getNatural(getString(key), def);
    }

    @Override
    public Long getNatural(final String key, final String candidate, final Long def) {
        Long result = getNatural(key, (Long) null);
        return result != null ? result : getNatural(candidate, def);
    }

    @Override
    public Long getNaturalLong(final URLOption<Long> option) {
        return option == null ? null : getNatural(option.getName(), option.getValue());
    }

    @Override
    public Long getNaturalLong(final URLBiOption<Long> option) {
        return option == null ? null : getNatural(option.getName(), option.getCandidate(), option.getValue());
    }

    @Override
    public Integer getNatural(final String key, final Integer def) {
        return Converts.getNatural(getString(key), def);
    }

    @Override
    public Integer getNatural(final String key, final String candidate, final Integer def) {
        Integer result = getNatural(key, (Integer) null);
        return result != null ? result : getNatural(candidate, def);
    }

    @Override
    public Integer getNaturalInt(final URLOption<Integer> option) {
        return option == null ? null : getNatural(option.getName(), option.getValue());
    }

    @Override
    public Integer getNaturalInt(final URLBiOption<Integer> option) {
        return option == null ? null : getNatural(option.getName(), option.getCandidate(), option.getValue());
    }

    @Override
    public Short getNatural(final String key, final Short def) {
        return Converts.getNatural(getString(key), def);
    }

    @Override
    public Short getNatural(final String key, final String candidate, final Short def) {
        Short result = getNatural(key, (Short) null);
        return result != null ? result : getNatural(key, def);
    }

    @Override
    public Short getNaturalShort(final URLOption<Short> option) {
        return option == null ? null : getNatural(option.getName(), option.getValue());
    }

    @Override
    public Short getNaturalShort(final URLBiOption<Short> option) {
        return option == null ? null : getNatural(option.getName(), option.getCandidate(), option.getValue());
    }

    @Override
    public Byte getNatural(final String key, final Byte def) {
        return Converts.getNatural(getString(key), def);
    }

    @Override
    public Byte getNatural(final String key, final String candidate, final Byte def) {
        Byte result = getNatural(key, (Byte) null);
        return result != null ? result : getNatural(candidate, def);
    }

    @Override
    public Byte getNaturalByte(final URLOption<Byte> option) {
        return option == null ? null : getNatural(option.getName(), option.getValue());
    }

    @Override
    public Byte getNaturalByte(final URLBiOption<Byte> option) {
        return option == null ? null : getNatural(option.getName(), option.getCandidate(), option.getValue());
    }

    @Override
    public Long getPositive(final String key, final Long def) {
        return Converts.getPositive(getString(key), def);
    }

    @Override
    public Long getPositive(final String key, final String candidate, final Long def) {
        Long result = getPositive(key, (Long) null);
        return result != null ? result : getPositive(candidate, def);
    }

    @Override
    public Long getPositiveLong(final URLOption<Long> option) {
        return option == null ? null : getPositive(option.getName(), option.getValue());
    }

    @Override
    public Long getPositiveLong(final URLBiOption<Long> option) {
        return option == null ? null : getPositive(option.getName(), option.getCandidate(), option.getValue());
    }

    @Override
    public Integer getPositive(final String key, final Integer def) {
        return Converts.getPositive(getString(key), def);
    }

    @Override
    public Integer getPositive(final String key, final String candidate, final Integer def) {
        Integer result = getPositive(key, (Integer) null);
        return result != null ? result : getPositive(candidate, def);
    }

    @Override
    public Integer getPositiveInt(final URLOption<Integer> option) {
        return option == null ? null : getPositive(option.getName(), option.getValue());
    }

    @Override
    public Integer getPositiveInt(final URLBiOption<Integer> option) {
        return option == null ? null : getPositive(option.getName(), option.getCandidate(), option.getValue());
    }

    @Override
    public Short getPositive(final String key, final Short def) {
        return Converts.getPositive(getString(key), def);
    }

    @Override
    public Short getPositive(final String key, final String candidate, final Short def) {
        Short result = getPositive(key, (Short) null);
        return result != null ? result : getPositive(candidate, def);
    }

    @Override
    public Short getPositiveShort(final URLOption<Short> option) {
        return option == null ? null : getPositive(option.getName(), option.getValue());
    }

    @Override
    public Short getPositiveShort(final URLBiOption<Short> option) {
        return option == null ? null : getPositive(option.getName(), option.getCandidate(), option.getValue());
    }

    @Override
    public Byte getPositive(final String key, final Byte def) {
        return Converts.getPositive(getString(key), def);
    }

    @Override
    public Byte getPositive(final String key, final String candidate, final Byte def) {
        Byte result = getPositive(key, (Byte) null);
        return result != null ? result : getPositive(candidate, def);
    }

    @Override
    public Byte getPositiveByte(final URLOption<Byte> option) {
        return option == null ? null : getPositive(option.getName(), option.getValue());
    }

    @Override
    public Byte getPositiveByte(final URLBiOption<Byte> option) {
        return option == null ? null : getPositive(option.getName(), option.getCandidate(), option.getValue());
    }

}
