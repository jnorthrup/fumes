# Fumes

Elite Dangerous non-refuelling route plotter

what if you could pilot an all-out engineered combat ship in Elite Dangerous further than your class 4 tank will normally take you in under 30 jumps?
Bring along every module crucial to the destination going in hot-- and the best part is you can skip the distractions at carriers and stations before and after?

Coming in Spring 2023 to theaters everywhere, the EDMC plugin absolutely noone is asking for... Fumes!


## galaxy.gz ingest

on my pc built in tmpfs (32GiB RAM, 12P+4E cores , 5/3.5GHz ) it takes 38 minutes to ingest the 65GiB galaxy.json.gz file:

 
`read 336.63 GiB in 38m 55.825937802s -- avg 39.91 KiLOC/json ,  147.62 MiB  per second; avgline size 3.69 KiB`

the working plan is to import the daily, and the full galaxy.json.gz files, and then prioritize based on smaller size

daemon updates would ping the server for HEAD digests and dispatch when something is new.

### indexes 

#### as btrfs compression

I checked this on my btrfs compressed volume mounted as `(rw,noatime,compress=lzo,ssd,discard=async,space_cache=v2)`

the isam indexes are apparently btrfs lzo friendly.  compsize says:

| Type  | Perc | Disk Usage | Uncompressed | Referenced |
|-------|-----:|-----------:|-------------:|-----------:|
| TOTAL |  41% |       3.5G |         8.4G |       8.4G |      
| none  | 100% |       1.3G |         1.3G |       1.3G |      
| lzo   |  30% |       2.2G |         7.1G |       7.1G |      

#### raw 

 * total bytes for gzip cache:  65G

 * total index bytes: 8.5G

| size | file                            |
|------|---------------------------------|
| 729M | galaxy.json.Id64.isam           |
| 117  | galaxy.json.Id64.isam.meta      |
| 5.7G | galaxy.json.Name.isam           |
| 123  | galaxy.json.Name.isam.meta      |
| 729M | galaxy.json.Seek.isam           |
| 117  | galaxy.json.Seek.isam.meta      |
| 365M | galaxy.json.X.isam              |
| 112  | galaxy.json.X.isam.meta         |
| 365M | galaxy.json.Y.isam              |
| 112  | galaxy.json.Y.isam.meta         |
| 365M | galaxy.json.Z.isam              |
| 112  | galaxy.json.Z.isam.meta         |
| 65G  | galaxy.json.gz                  |
| 240M | galaxy.json.gzi                 |
| 707K | galaxy_1day.json.Id64.isam      |
| 117  | galaxy_1day.json.Id64.isam.meta |
| 5.6M | galaxy_1day.json.Name.isam      |
| 123  | galaxy_1day.json.Name.isam.meta |
| 709K | galaxy_1day.json.Seek.isam      |
| 117  | galaxy_1day.json.Seek.isam.meta |
| 355K | galaxy_1day.json.X.isam         |
| 112  | galaxy_1day.json.X.isam.meta    |
| 355K | galaxy_1day.json.Y.isam         |
| 112  | galaxy_1day.json.Y.isam.meta    |
| 355K | galaxy_1day.json.Z.isam         |
| 112  | galaxy_1day.json.Z.isam.meta    |
| 563M | galaxy_1day.json.gz             |
| 2.0M | galaxy_1day.json.gzi            |
