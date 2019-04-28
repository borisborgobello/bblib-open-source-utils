## JARUpdater open source files JFX

### Author

Author : Boris Borgobello

### Library

A very old jar created using JavaFX. Distributing this JAR file + a config file is enough to allow a user to download/update a software and run it.

Config file is a JSON.

- Set platform
- Set url to check for new versions
- Set export folder
- Set bin path after unzipping
- Set exec command

User needs to start the app always using this updater.

```json
{
	"is_mac_updater" : "false",
	"basic_auth":"",
	"url_check_last_bin" : "https://AAA.com.vn/api/v1/globfiles/get_last_thorder_win",
    "url_bin_folder" : "https://AAA.com.vn",
    "export_folder" : "./../app",
    "bin_path" : "./../app/IStitchJFX.exe",
    "exec_command" : "./../app/IStitchJFX.exe"
}	
```