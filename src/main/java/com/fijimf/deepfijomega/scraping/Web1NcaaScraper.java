package com.fijimf.deepfijomega.scraping;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@Component
public class Web1NcaaScraper {
    public static List<String> teamKeys() {
        return new ArrayList<>(teamKeys.keySet());
    }

    public RequestResult scrape(int year, String teamKey) {
        String url = urlFromKey(year, teamKey);
        LocalDateTime start = LocalDateTime.now();
        return null;
    }

    public String urlFromKey(int year, String k) {
        Integer id = teamKeys.get(k);
        return "http://web1.ncaa.org/stats/exec/records?doWhat=display&useData=CAREER&sportCode=MBB&academicYear=" + year + "&orgId=" + id + "&division=1&playerId=-100";
    }

//    public List<UpdateCandidate> extractUpdates(String key, Node root) {
//        NodeSeq rows  = extractGameRows(root);
//        rows.flatMap(extractGame(key.toInt,_)).toList
//    }

    public List<Optional<UpdateCandidate>> extractGameRows(Document root) {
        Elements elements = root.select("tr");
        return elements
                .stream()
                .map((elem) -> extractGame(0, elem))
                .collect(Collectors.toList());
    }
//
//    public List<UpdateCandidate> parseGames(String key, String data) {
//        loadFromString(data) match {
//            case Success(root)=>
//                extractUpdates(key, root)
//            case Failure(_)=>List.empty[UpdateCandidate]
//        }
//    }

    public Document loadFromReader(Reader r) throws IOException {
        return loadFromString(IOUtils.toString(r));
    }

    public Document loadFromString(String s) {
        return Jsoup.parse(s);
    }


    public Optional<UpdateCandidate> extractGame(Integer teamId, Element row) {
        if (teamIds.containsKey(teamId)) {
            String teamKey = teamIds.get(teamId);
            Pattern pattern = Pattern.compile("javascript:showTeamResults\\((\\d+)\\);");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            List<Element> cells = new ArrayList<>(row.select("td"));
            if (cells.size() > 6 && cells.get(0).ownText().contains("%")) {
                String href = cells.get(0).child(0).attr("href");
                Matcher matcher = pattern.matcher(href);
                if (matcher.matches()) {
                    int opp = Integer.parseInt(matcher.group(1));
                    if (teamIds.containsKey(opp)) {
                        String oppKey = teamIds.get(opp);
                        LocalDate date = LocalDate.parse(cells.get(1).ownText(), formatter);
                        int teamScore = Integer.parseInt(cells.get(2).ownText());
                        int oppScore = Integer.parseInt(cells.get(3).ownText());
                        boolean isNeutral = cells.get(4).ownText().trim().equalsIgnoreCase("neutral");
                        System.out.println(teamKey + "," + teamScore + "," + oppKey + "," + oppScore + "," + date + "," + isNeutral);
                        System.out.println(cells.get(5).ownText());
                        System.out.println(cells.get(6).ownText());
                        System.out.println(cells.get(7).ownText());
                       // new UpdateCandidate(date.atTime(19,0),)
                    }
                }
            }
        }
        return Optional.empty();
    }

//    private def buildUpdateCandidate(teamId: Int, oppId: Int, dateTime: LocalDateTime, homeAway: String, score: Int, oppScore: Int, otString: String, team: String, opponent: String): Option[UpdateCandidate] = {
//        val (ht, hs, at, as) = if (homeAway.equalsIgnoreCase("home")) {
//            (team, score, opponent, oppScore)
//        } else if (homeAway.equalsIgnoreCase("away")) {
//            (opponent, oppScore, team, score)
//        } else {
//            if (teamId < oppId) {
//                (team, score, opponent, oppScore)
//            } else {
//                (opponent, oppScore, team, score)
//            }
//        }
//        if (team === ht)
//            Some(UpdateCandidate(
//                    dateTime,
//                    ht,
//                    at,
//                    None,
//                    Some(homeAway.equalsIgnoreCase("neutral")),
//                    Some(hs),
//                    Some(as),
//                    Some(otStringToNumPeriods(otString))
//            )) else None
//    }
//
//    def oppNodeToCode(oppNode:Node): Int ={
//        val extractor: Regex = """javascript:showTeamResults\((\d+)\);""".r
//                (for {
//            a <- oppNode \ "a"
//            hr <- a.attribute("href")
//            hr0 <- hr.headOption
//        }yield {
//            hr0.text.trim match {
//                case extractor(t) => t.toInt
//                case _ => -1
//            }
//        }).headOption.getOrElse(-1)
//    }
//
//    def otStringToNumPeriods(s:String): Int ={
//        s.toLowerCase.trim match {
//            case "-"=>2
//            case "1 ot"=>3
//            case "2 ot"=>4
//            case "3 ot"=>5
//            case "4 ot"=>6
//            case "5 ot"=>7
//            case "6 ot"=>8
//            case _ =>2
//        }
//    }
//
//    private def checkAttributeEquals(node:Node, attribute: String, value:String):Boolean = {
//        node.attributes.exists(_.value.text === value)
//    }
//
//    def checkTdForPercent(node:Node):Boolean ={
//        val option: Option[Node] = (node \ "td").headOption
//        option.exists(_.text.contains("%"))
//    }
//}
//


    private static Map<String, Integer> teamKeys = Map.<String, Integer>ofEntries(
            entry("abilene-christian", 2),
            entry("air-force", 721),
            entry("akron", 5),
            entry("alabama", 8),
            entry("alabama-am", 6),
            entry("alabama-st", 7),
            entry("albany-ny", 14),
            entry("alcorn", 17),
            entry("am-corpus-chris", 26172),
            entry("american", 23),
            entry("appalachian-st", 27),
            entry("arizona", 29),
            entry("arizona-st", 28),
            entry("ark-pine-bluff", 2678),
            entry("arkansas", 31),
            entry("arkansas-st", 30),
            entry("army", 725),
            entry("auburn", 37),
            entry("austin-peay", 43),
            entry("bakersfield", 94),
            entry("ball-st", 47),
            entry("baylor", 51),
            entry("belmont", 14927),
            entry("bethune-cookman", 61),
            entry("binghamton", 62),
            entry("boise-st", 66),
            entry("boston-college", 67),
            entry("boston-u", 68),
            entry("bowling-green", 71),
            entry("bradley", 72),
            entry("brown", 80),
            entry("bryant", 81),
            entry("bucknell", 83),
            entry("buffalo", 86),
            entry("butler", 87),
            entry("byu", 77),
            entry("cal-poly", 90),
            entry("cal-st-fullerton", 97),
            entry("cal-st-northridge", 101),
            entry("california", 107),
            entry("campbell", 115),
            entry("canisius", 116),
            entry("central-ark", 1004),
            entry("central-conn-st", 127),
            entry("central-mich", 129),
            entry("charleston-so", 48),
            entry("charlotte", 458),
            entry("chattanooga", 693),
            entry("chicago-st", 136),
            entry("cincinnati", 140),
            entry("citadel", 141),
            entry("clemson", 147),
            entry("cleveland-st", 148),
            entry("coastal-caro", 149),
            entry("col-of-charleston", 1014),
            entry("colgate", 153),
            entry("colorado", 157),
            entry("colorado-st", 156),
            entry("columbia", 158),
            entry("coppin-st", 165),
            entry("cornell", 167),
            entry("creighton", 169),
            entry("dartmouth", 172),
            entry("davidson", 173),
            entry("dayton", 175),
            entry("delaware", 180),
            entry("delaware-st", 178),
            entry("denver", 183),
            entry("depaul", 176),
            entry("detroit", 184),
            entry("drake", 189),
            entry("drexel", 191),
            entry("duke", 193),
            entry("duquesne", 194),
            entry("east-carolina", 196),
            entry("east-tenn-st", 198),
            entry("eastern-ill", 201),
            entry("eastern-ky", 202),
            entry("eastern-mich", 204),
            entry("eastern-wash", 207),
            entry("elon", 1068),
            entry("evansville", 219),
            entry("fairfield", 220),
            entry("fairleigh-dickinson", 222),
            entry("fgcu", 28755),
            entry("fiu", 231),
            entry("fla-atlantic", 229),
            entry("florida", 235),
            entry("florida-am", 228),
            entry("florida-st", 234),
            entry("fordham", 236),
            entry("fresno-st", 96),
            entry("furman", 244),
            entry("ga-southern", 253),
            entry("gardner-webb", 1092),
            entry("george-mason", 248),
            entry("george-washington", 249),
            entry("georgetown", 251),
            entry("georgia", 257),
            entry("georgia-st", 254),
            entry("georgia-tech", 255),
            entry("gonzaga", 260),
            entry("grambling", 261),
            entry("grand-canyon", 1104),
            entry("green-bay", 794),
            entry("hampton", 270),
            entry("hartford", 272),
            entry("harvard", 275),
            entry("hawaii", 277),
            entry("high-point", 19651),
            entry("hofstra", 283),
            entry("holy-cross", 285),
            entry("houston", 288),
            entry("houston-baptist", 287),
            entry("howard", 290),
            entry("idaho", 295),
            entry("idaho-st", 294),
            entry("ill-chicago", 302),
            entry("illinois", 301),
            entry("illinois-st", 299),
            entry("incarnate-word", 2743),
            entry("indiana", 306),
            entry("indiana-st", 305),
            entry("iona", 310),
            entry("iowa", 312),
            entry("iowa-st", 311),
            entry("ipfw", 308),
            entry("iupui", 2699),
            entry("jackson-st", 314),
            entry("jacksonville", 316),
            entry("jacksonville-st", 315),
            entry("james-madison", 317),
            entry("kansas", 328),
            entry("kansas-st", 327),
            entry("kennesaw-st", 1157),
            entry("kent-st", 331),
            entry("kentucky", 334),
            entry("la-lafayette", 671),
            entry("la-monroe", 498),
            entry("la-salle", 340),
            entry("lafayette", 342),
            entry("lamar", 346),
            entry("lehigh", 352),
            entry("liberty", 355),
            entry("lipscomb", 28600),
            entry("long-beach-st", 99),
            entry("long-island", 361),
            entry("longwood", 363),
            entry("louisiana-tech", 366),
            entry("louisville", 367),
            entry("loyola-il", 371),
            entry("loyola-maryland", 369),
            entry("loyola-marymount", 370),
            entry("lsu", 365),
            entry("maine", 380),
            entry("manhattan", 381),
            entry("marist", 386),
            entry("marquette", 387),
            entry("marshall", 388),
            entry("maryland", 392),
            entry("mass-lowell", 368),
            entry("massachusetts", 400),
            entry("mcneese-st", 402),
            entry("memphis", 404),
            entry("mercer", 406),
            entry("miami-fl", 415),
            entry("miami-oh", 414),
            entry("michigan", 418),
            entry("michigan-st", 416),
            entry("middle-tenn", 419),
            entry("milwaukee", 797),
            entry("minnesota", 428),
            entry("mississippi-st", 430),
            entry("mississippi-val", 432),
            entry("missouri", 434),
            entry("missouri-st", 435),
            entry("monmouth", 439),
            entry("montana", 441),
            entry("montana-st", 440),
            entry("morehead-st", 444),
            entry("morgan-st", 446),
            entry("mt-st-marys", 450),
            entry("murray-st", 454),
            entry("navy", 726),
            entry("nc-at", 488),
            entry("nc-central", 489),
            entry("neb-omaha", 464),
            entry("nebraska", 463),
            entry("nevada", 466),
            entry("new-hampshire", 469),
            entry("new-mexico", 473),
            entry("new-mexico-st", 472),
            entry("new-orleans", 474),
            entry("niagara", 482),
            entry("nicholls-st", 483),
            entry("njit", 471),
            entry("norfolk-st", 485),
            entry("north-carolina", 457),
            entry("north-carolina-st", 490),
            entry("north-dakota", 494),
            entry("north-dakota-st", 493),
            entry("north-florida", 2711),
            entry("north-texas", 497),
            entry("northeastern", 500),
            entry("northern-ariz", 501),
            entry("northern-colo", 502),
            entry("northern-ill", 503),
            entry("northern-ky", 505),
            entry("northwestern", 509),
            entry("northwestern-st", 508),
            entry("notre-dame", 513),
            entry("oakland", 514),
            entry("ohio", 519),
            entry("ohio-st", 518),
            entry("oklahoma", 522),
            entry("oklahoma-st", 521),
            entry("old-dominion", 523),
            entry("ole-miss", 433),
            entry("oral-roberts", 527),
            entry("oregon", 529),
            entry("oregon-st", 528),
            entry("pacific", 534),
            entry("penn", 540),
            entry("penn-st", 539),
            entry("pepperdine", 541),
            entry("pittsburgh", 545),
            entry("portland", 551),
            entry("portland-st", 550),
            entry("prairie-view", 553),
            entry("presbyterian", 1320),
            entry("princeton", 554),
            entry("providence", 556),
            entry("purdue", 559),
            entry("quinnipiac", 562),
            entry("radford", 563),
            entry("rhode-island", 572),
            entry("rice", 574),
            entry("richmond", 575),
            entry("rider", 576),
            entry("robert-morris", 579),
            entry("rutgers", 587),
            entry("sacramento-st", 102),
            entry("sacred-heart", 590),
            entry("saint-josephs", 606),
            entry("saint-louis", 609),
            entry("sam-houston-st", 624),
            entry("samford", 625),
            entry("san-diego", 627),
            entry("san-diego-st", 626),
            entry("san-francisco", 629),
            entry("san-jose-st", 630),
            entry("santa-clara", 631),
            entry("savannah-st", 632),
            entry("seattle", 1356),
            entry("seton-hall", 635),
            entry("siena", 639),
            entry("siu-edwardsville", 660),
            entry("smu", 663),
            entry("south-ala", 646),
            entry("south-carolina", 648),
            entry("south-carolina-st", 647),
            entry("south-dakota", 650),
            entry("south-dakota-st", 649),
            entry("south-fla", 651),
            entry("southeast-mo-st", 654),
            entry("southeastern-la", 655),
            entry("southern-california", 657),
            entry("southern-ill", 659),
            entry("southern-miss", 664),
            entry("southern-u", 665),
            entry("southern-utah", 667),
            entry("st-bonaventure", 596),
            entry("st-francis-ny", 599),
            entry("st-francis-pa", 600),
            entry("st-johns-ny", 603),
            entry("st-marys-ca", 610),
            entry("st-peters", 617),
            entry("stanford", 674),
            entry("stephen-f-austin", 676),
            entry("stetson", 678),
            entry("stony-brook", 683),
            entry("syracuse", 688),
            entry("tcu", 698),
            entry("temple", 690),
            entry("tennessee", 694),
            entry("tennessee-st", 691),
            entry("tennessee-tech", 692),
            entry("texas", 703),
            entry("texas-am", 697),
            entry("texas-arlington", 702),
            entry("texas-southern", 699),
            entry("texas-st", 670),
            entry("texas-tech", 700),
            entry("toledo", 709),
            entry("towson", 711),
            entry("troy", 716),
            entry("tulane", 718),
            entry("tulsa", 719),
            entry("uab", 9),
            entry("ualr", 32),
            entry("uc-davis", 108),
            entry("uc-irvine", 109),
            entry("uc-riverside", 111),
            entry("uc-santa-barbara", 104),
            entry("ucf", 128),
            entry("ucla", 110),
            entry("uconn", 164),
            entry("umbc", 391),
            entry("umes", 393),
            entry("umkc", 2707),
            entry("unc-asheville", 456),
            entry("unc-greensboro", 459),
            entry("unc-wilmington", 460),
            entry("uni", 504),
            entry("unlv", 465),
            entry("usc-upstate", 10411),
            entry("ut-martin", 695),
            entry("utah", 732),
            entry("utah-st", 731),
            entry("utah-valley", 30024),
            entry("utep", 704),
            entry("utrgv", 536),
            entry("utsa", 706),
            entry("valparaiso", 735),
            entry("vanderbilt", 736),
            entry("vcu", 740),
            entry("vermont", 738),
            entry("villanova", 739),
            entry("virginia", 746),
            entry("virginia-tech", 742),
            entry("vmi", 741),
            entry("wagner", 748),
            entry("wake-forest", 749),
            entry("washington", 756),
            entry("washington-st", 754),
            entry("weber-st", 758),
            entry("west-virginia", 768),
            entry("western-caro", 769),
            entry("western-ill", 771),
            entry("western-ky", 772),
            entry("western-mich", 774),
            entry("wichita-st", 782),
            entry("william-mary", 786),
            entry("winthrop", 792),
            entry("wisconsin", 796),
            entry("wofford", 2915),
            entry("wright-st", 810),
            entry("wyoming", 811),
            entry("xavier", 812),
            entry("yale", 813),
            entry("youngstown-st", 817)
    );

    private static final Map<Integer, String> teamIds = teamKeys.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
}
