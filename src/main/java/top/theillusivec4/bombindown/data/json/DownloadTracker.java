package top.theillusivec4.bombindown.data.json;

public record DownloadTracker(String url, String video, String output, String status,
                              boolean metadata, boolean images) {

}
