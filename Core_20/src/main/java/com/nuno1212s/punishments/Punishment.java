package com.nuno1212s.punishments;

import com.nuno1212s.main.MainData;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

public class Punishment {

    @Getter
    private PunishmentType punishmentType;

    @Getter
    private long startTime, durationInMillis;

    @Getter
    private String reason;

    public Punishment(PunishmentType type, long startingTime, long duration, String reason) {
        this.punishmentType = type;
        this.startTime = startingTime;
        this.durationInMillis = duration;
        this.reason = reason;

        if (this.reason.length() >= 200) {
            this.reason = "";
            throw new IllegalArgumentException("Reason can't be longer than 200 characters");
        }
    }

    public Punishment(String data) {
        String[] dataSplit = data.split(":");
        this.punishmentType = PunishmentType.valueOf(dataSplit[0]);
        this.startTime = Long.parseLong(dataSplit[1]);
        this.durationInMillis = Long.parseLong(dataSplit[2]);
        this.reason = dataSplit[3];
    }

    public boolean hasExpired() {
        return this.startTime + durationInMillis < System.currentTimeMillis();
    }

    public String timeToString() {
        StringBuilder builder = new StringBuilder("");

        long duration = durationInMillis;
        long days = TimeUnit.MILLISECONDS.toDays(durationInMillis);
        if (days > 0) {
            builder.append(days);
            builder.append(MainData.getIns().getMessageManager().getMessage("DAYS").toString());
            builder.append(" ");
            duration -= TimeUnit.DAYS.toMillis(days);
        }

        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        if (hours > 0) {
            builder.append(hours);
            builder.append(MainData.getIns().getMessageManager().getMessage("HOURS").toString());
            builder.append(" ");
            duration -= TimeUnit.HOURS.toMillis(hours);
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        if (minutes > 0) {
            builder.append(minutes);
            builder.append(MainData.getIns().getMessageManager().getMessage("MINUTES").toString());
            builder.append(" ");
            duration -= TimeUnit.MINUTES.toMillis(minutes);
        }

        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        if (seconds > 0) {
            builder.append(seconds);
            builder.append(MainData.getIns().getMessageManager().getMessage("SECONDS").toString());
            builder.append(" ");
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return punishmentType.toString() + ":" + startTime + ":" + durationInMillis + ":" + reason;
    }

    public enum PunishmentType {
        BAN,
        MUTE
    }

}
