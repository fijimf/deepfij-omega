package com.fijimf.deepfijomega.scraping;

import org.apache.commons.lang3.math.NumberUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Casablanca {
    private List<GameWrapper> games;
    private String inputMD5Sum;
    private String updated_at;

    public List<GameWrapper> getGames() {
        return games;
    }

    public void setGames(List<GameWrapper> games) {
        this.games = games;
    }

    public String getInputMD5Sum() {
        return inputMD5Sum;
    }

    public void setInputMD5Sum(String inputMD5Sum) {
        this.inputMD5Sum = inputMD5Sum;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public static class GameWrapper {
        private Game game;

        public Game getGame() {
            return game;
        }

        public void setGame(Game game) {
            this.game = game;
        }

        public boolean isSameDate(LocalDate d) {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            try {
                LocalDate date = LocalDate.parse(game.getStartDate(), formatter);
                return date.isEqual(d) || date.isEqual(d.plusDays(1)); //Hawaii late games
            } catch (Exception e) {
                return false;
            }
        }
    }

    public static class Game {
        private TeamInfo away;
        private String bracketId;
        private String bracketRegion;
        private String bracketRound;
        private String contestClock;
        private String currentPeriod;
        private String finalMessage;
        private String gameID;
        private String gameState;
        private TeamInfo home;
        private boolean liveVideoEnabled;
        private String network;
        private String startDate;
        private String startTime;
        private String startTimeEpoch;
        private String title;
        private String url;
        private String videoState;


        // Getter Methods

        public TeamInfo getAway() {
            return away;
        }

        public String getBracketId() {
            return bracketId;
        }

        public String getBracketRegion() {
            return bracketRegion;
        }

        public String getBracketRound() {
            return bracketRound;
        }

        public String getContestClock() {
            return contestClock;
        }

        public String getCurrentPeriod() {
            return currentPeriod;
        }

        public String getFinalMessage() {
            return finalMessage;
        }

        public String getGameID() {
            return gameID;
        }

        public String getGameState() {
            return gameState;
        }

        public TeamInfo getHome() {
            return home;
        }

        public boolean getLiveVideoEnabled() {
            return liveVideoEnabled;
        }

        public String getNetwork() {
            return network;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getStartTimeEpoch() {
            return startTimeEpoch;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }

        public String getVideoState() {
            return videoState;
        }

        // Setter Methods

        public void setAway(TeamInfo away) {
            this.away = away;
        }

        public void setBracketId(String bracketId) {
            this.bracketId = bracketId;
        }

        public void setBracketRegion(String bracketRegion) {
            this.bracketRegion = bracketRegion;
        }

        public void setBracketRound(String bracketRound) {
            this.bracketRound = bracketRound;
        }

        public void setContestClock(String contestClock) {
            this.contestClock = contestClock;
        }

        public void setCurrentPeriod(String currentPeriod) {
            this.currentPeriod = currentPeriod;
        }

        public void setFinalMessage(String finalMessage) {
            this.finalMessage = finalMessage;
        }

        public void setGameID(String gameID) {
            this.gameID = gameID;
        }

        public void setGameState(String gameState) {
            this.gameState = gameState;
        }

        public void setHome(TeamInfo home) {
            this.home = home;
        }

        public void setLiveVideoEnabled(boolean liveVideoEnabled) {
            this.liveVideoEnabled = liveVideoEnabled;
        }

        public void setNetwork(String network) {
            this.network = network;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public void setStartTimeEpoch(String startTimeEpoch) {
            this.startTimeEpoch = startTimeEpoch;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setVideoState(String videoState) {
            this.videoState = videoState;
        }
    }

    public static class ConferenceNames {
        private String conferenceDisplay;
        private String conferenceDivision;
        private String conferenceName;
        private String conferenceSeo;


        // Getter Methods

        public String getConferenceDisplay() {
            return conferenceDisplay;
        }

        public String getConferenceDivision() {
            return conferenceDivision;
        }

        public String getConferenceName() {
            return conferenceName;
        }

        public String getConferenceSeo() {
            return conferenceSeo;
        }

        // Setter Methods

        public void setConferenceDisplay(String conferenceDisplay) {
            this.conferenceDisplay = conferenceDisplay;
        }

        public void setConferenceDivision(String conferenceDivision) {
            this.conferenceDivision = conferenceDivision;
        }

        public void setConferenceName(String conferenceName) {
            this.conferenceName = conferenceName;
        }

        public void setConferenceSeo(String conferenceSeo) {
            this.conferenceSeo = conferenceSeo;
        }
    }

    public static class TeamInfo {
        ConferenceNames ConferenceNamesObject;
        private String description;
        Names NamesObject;
        private String rank;
        private String score;
        private String seed;
        private boolean winner;


        // Getter Methods

        public ConferenceNames getConferenceNames() {
            return ConferenceNamesObject;
        }

        public String getDescription() {
            return description;
        }

        public Names getNames() {
            return NamesObject;
        }

        public String getRank() {
            return rank;
        }

        public String getScore() {
            return score;
        }

        public String getSeed() {
            return seed;
        }

        public boolean getWinner() {
            return winner;
        }

        // Setter Methods

        public void setConferenceNames(ConferenceNames conferenceNamesObject) {
            this.ConferenceNamesObject = conferenceNamesObject;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setNames(Names namesObject) {
            this.NamesObject = namesObject;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public void setSeed(String seed) {
            this.seed = seed;
        }

        public void setWinner(boolean winner) {
            this.winner = winner;
        }
    }

    public static class Names {
        private String char6;
        private String full;
        private String seo;
        private String shortName;


        // Getter Methods

        public String getChar6() {
            return char6;
        }

        public String getFull() {
            return full;
        }

        public String getSeo() {
            return seo;
        }

        public String getShort() {
            return shortName;
        }

        // Setter Methods

        public void setChar6(String char6) {
            this.char6 = char6;
        }

        public void setFull(String full) {
            this.full = full;
        }

        public void setSeo(String seo) {
            this.seo = seo;
        }

        public void setShort(String shortName) {
            this.shortName = shortName;
        }
    }

    public List<UpdateCandidate> extractUpdates(LocalDate date) {
        return games
                .stream()
                .filter(g -> g.isSameDate(date))
                .map(Casablanca::wrapperToUpdate)
                .collect(Collectors.toList());
    }

    private static boolean isFinal(GameWrapper gw) {
        return gw.getGame().gameState.equalsIgnoreCase("final");
    }

    private static LocalDateTime startTime(GameWrapper gw) {
        long ste = Long.parseLong(gw.getGame().getStartTimeEpoch());
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(ste), ZoneId.of("America/New_York"));
    }


    private static UpdateCandidate wrapperToUpdate(GameWrapper gw) {
        String homeKey = gw.getGame().getHome().getNames().getSeo();
        String awayKey = gw.getGame().getAway().getNames().getSeo();
        LocalDateTime startTime = startTime(gw);
        if (isFinal(gw)) {

            Integer homeScore = NumberUtils.toInt(gw.getGame().getHome().score);
            Integer awayScore = NumberUtils.toInt(gw.getGame().getAway().score);
            String period = gw.getGame().getCurrentPeriod().toLowerCase();
            Integer numPeriods =
                    getNumPeriods(period);
            return new UpdateCandidate(startTime, homeKey, awayKey, null, null, homeScore, awayScore, numPeriods);
        } else {
            return new UpdateCandidate(startTime, homeKey, awayKey, null, null, null, null, null);
        }
    }

    public static Integer getNumPeriods(String period) {
        if (period == null) {
            return 2;
        } else {
            String p = period.toLowerCase().trim();
            if (p.equals("")) {
                return 2;
            } else if (p.equals("final")) {
                return 2;
            } else {
                Matcher singleOt = Pattern.compile("(final/ot)|(final\\(ot\\))|(final \\(ot\\))").matcher(p);

                if (singleOt.matches()) {
                    return 3;
                } else {
                    Matcher multipleOT1 = Pattern.compile("final/([2-9])ot").matcher(p);
                    Matcher multipleOT2 = Pattern.compile("final\\(([2-9])ot\\)").matcher(p);
                    Matcher multipleOT3 = Pattern.compile("final \\(([2-9])ot\\)").matcher(p);
                    if (multipleOT1.matches()) {
                        return Integer.parseInt(multipleOT1.group(1)) + 2;
                    } else if (multipleOT2.matches()) {
                        return Integer.parseInt(multipleOT2.group(1)) + 2;
                    } else if (multipleOT3.matches()) {
                        return Integer.parseInt(multipleOT3.group(1)) + 2;
                    } else {
                        return 2;
                    }
                }
            }
        }

    }
}