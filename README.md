# Bombin' DL

A Java application providing an easy-to-use GUI for searching and downloading shows and videos from Giant Bomb using the
Giant Bomb API.

## Features

### Search

All Giant Bomb videos and shows are populated into two master lists respectively and are searchable by either title or
GUID (as identified by the Giant Bomb API). Additionally, filters can be applied to the search for Premium membership
status and date ranges.

### Download

Bombin' DL provides a simple download manager that can be used to directly download videos or entire shows from within
the application. Multiple shows and videos can be queued for downloads and selections can be made to pick out individual
videos in a show without downloading it in its entirety.

* **Highest available quality**
  * The Giant Bomb API sometimes does not the return the highest quality video from its API, but this application will
automatically detect and pull them when needed
* **Simultaneous downloads**
  * Multiple concurrent downloads are supported, defaults to 3 simultaneous connections
* **Download progress tracking**
  * Downloads in progress are saved between sessions and will restart upon reloading
* **Custom file output names**
  * File name templates allow for customizable file names based on title, GUID, and publication date
* **Download metadata and thumbnails**
  * Optionally, metadata and images from the video can be extracted alongside the downloaded file
* **Native rate limiting**
  * Bombin' DL automatically limits the rate of requests it sends out. This impacts speed but prevents potential
misuse of the Giant Bomb API.

## Requirements

* [Java 17+](https://adoptium.net/)
* [Giant Bomb API Key](https://www.giantbomb.com/api/)

## Installation

Bombin' DL is a portable application that doesn't require an installation.

First, check to make sure you have the pre-requisites. You will need to install Java 17 or higher as well as have a
Giant Bomb account to retrieve a personal API key.

Then, download the latest binary from the [releases page](https://github.com/TheIllusiveC4/bombin-down/releases) and
execute the jar file inside your desired directory. On launch, Bombin' DL will additionally create a sub-directory
named `bombin-down` to store all its data.

## How to Use

Upon starting the application for the first time, you will be prompted to enter your API key. An API key must be entered
before you can proceed to the rest of the program. If you are not a Giant Bomb member, you will need to sign up and then
go to [this page](https://www.giantbomb.com/api/) to receive your API key.

Please also make sure to check the box underneath the key if you are
a Giant Bomb Premium subscriber so that the application can be made aware that you can download premium videos.

If it is your first time launching the program, or it has been at least a day since the last launch, Bombin' DL will
then spend time updating its list of available videos and shows. This could take anywhere between a few seconds and
a few minutes depending on the number of videos to process, this is completely normal and expected. After this is done,
the results are cached and future launches will skip this process until the following day.

Once the application has made it to the main screen, you will see three tabs.

* **Shows** - The Shows tab can be used to search and filter through all available Giant Bomb shows.
* **Videos** - The Videos tab can be used to search and filter through all available Giant Bomb videos.
* **Downloads** - The Downloads tab can be used to view and manage your current and completed downloads.

### Choosing Videos and Shows

In order to download a video from the Videos tab, you need to select the videos by clicking on the checkbox in their row
on the list. This will add that video to a pending list of selections. Multiple videos can be selected.

Once ready, you can click the "Download" in the upper right corner of the table to proceed. You will be taken to a
separate screen with your chosen videos for confirmation. At this point, you can de-select certain videos if necessary.

From this screen, there are two options. The "Download" button will queue all of your selections for download in the
application's download manager. The "Export to TXT" button will instead export a `txt` file into the `bombin-down/exports`
directory that contains your list of downloads as a list of urls. This is useful for people who want to use their favored
download managers instead of Bombin' DL's very rudimentary one.

The process for choosing shows in the Shows tab is very similar to videos. The main difference is that you will only see
shows at first and you will need to select "Download" to be taken to the second screen where you will find each episode
from the show listed out to you for further selection before confirmation.

### Downloading

In the "Downloads" tab, you will find every completed, in-progress, and queued download. Every download that you request
will be queued at first and will begin once it finds an available connection. The default number of simultaneous
downloads is 3, but can be increased or decreased based on preference.

Downloads can be cancelled or restarted as needed. In-progress downloads that are cancelled or interrupted will not keep
their progress and will need to be restarted from the beginning.

Downloads are saved between sessions and any downloads from a previous session that had not yet been completed will be
re-queued upon loading up the application again.

There is an option to "Import from GUID File". From here, you can select any `txt` file that contains a list of video
GUIDs, separated by line. Bombin' DL will parse and download all videos listed in the file. This is useful for those
who know which specific videos they want to download and do not want to manually search and select from the application.
