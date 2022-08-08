# Setup Scrape

## ESPN
Seasons ->
Teams -> s3 -> Process -> Stage [Team]
Team Conference -> s3 -? Process -> Stage [TeamConference]
Scoreboard -> s3-> -> Process -> Stage [Game]

## NCAA
Map & augment team


### Team

SchoolName
Nickname
LongName
Slug
Key
ShortKey
EspnID
NcaaId
Color1
Color2
EspnLogoUrl
NcaaLogoUrl
Twitter
Instagram
Facebook
Website

### Game

Date
Time
HomeTeam
AwayTeam
HomeScore
AwayScore
IsFinal
NumPeriods
IsConfTournament
IsNcaaTournament
Favorite
Line
OverUnder
Location
Attendance
EspnID

