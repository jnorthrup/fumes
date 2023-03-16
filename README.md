# Fumes

Elite Dangerous non-refuelling route plotter

what if you could pilot an all-out engineered combat ship in Elite Dangerous further than your class 4 tank will normally take you in under 30 jumps?
Bring along every module crucial to the destination going in hot-- and the best part is you can skip the distractions at carriers and stations before and after?

Coming in Spring 2023 to theaters everywhere, the EDMC plugin absolutely noone is asking for... Fumes!


# galaxy.gz ingest

on my pc built in tmpfs (32GiB RAM, 12P+4E cores , 5/3.5GHz ) it takes 38 minutes to ingest the 65GiB galaxy.json.gz file:

`$ time ./fumes ingest galaxy.json.gz`
`read 336.63 GiB in 38m 55.825937802s -- avg 39.91 KiLOC/json ,  147.62 MiB  per second; avgline size 3.69 KiB`

the working plan is to import the daily, and the full galaxy.json.gz files, and then prioritize based on smaller size

daemon udpates would ping the server for HEAD digests and dispatch when something is new.

| size | file
|------|------------------------
| 729M | galaxy.json.Id64.isam
 | 117  | galaxy.json.Id64.isam.meta
| 5.7G | galaxy.json.Name.isam
 | 123  | galaxy.json.Name.isam.meta
| 729M | galaxy.json.Seek.isam
 | 117  | galaxy.json.Seek.isam.meta
| 365M | galaxy.json.X.isam
 | 112  | galaxy.json.X.isam.meta
| 365M | galaxy.json.Y.isam
 | 112  | galaxy.json.Y.isam.meta
| 365M | galaxy.json.Z.isam
 | 112  | galaxy.json.Z.isam.meta
 | 65G  | galaxy.json.gz
| 240M | galaxy.json.gzi
| 707K | galaxy_1day.json.Id64.isam
 | 117  | galaxy_1day.json.Id64.isam.meta
| 5.6M | galaxy_1day.json.Name.isam
 | 123  | galaxy_1day.json.Name.isam.meta
| 709K | galaxy_1day.json.Seek.isam
 | 117  | galaxy_1day.json.Seek.isam.meta
| 355K | galaxy_1day.json.X.isam
 | 112  | galaxy_1day.json.X.isam.meta
| 355K | galaxy_1day.json.Y.isam
 | 112  | galaxy_1day.json.Y.isam.meta
| 355K | galaxy_1day.json.Z.isam
 | 112  | galaxy_1day.json.Z.isam.meta
| 563M | galaxy_1day.json.gz
| 2.0M | galaxy_1day.json.gzi
total bytes for all:  75G
total isam bytes:  3.1G