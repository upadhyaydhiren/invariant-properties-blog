#Sample backup scripts for Linux (Ubuntu)

# Introduction #

Everyone knows that backups are important. Everyone knows it's never done if you have to do it by hand - it's important to automate this task.

These scripts can be used on Linux systems. They assume that backups are written to a separate physical disk mounted at /backups. Even better would be using NFS to mount a disk on a second system since that reduces the risk of a dying system scribbling over your backups.

### Purpose ###

There are three distinct reasons to maintain backups.

_System Restoration._ You lost a disk and want to recover the data. You only need the most recent snapshot.
In fact the best approach is to maintain a 'hot copy' using rsync run at least once an hour.

_Deleted File Recovery._ You accidently deleted a file and want to restore it. You need a succession of
snapshots, depending upon how sensitive you are to lost data. Historically this has been widely implemented
using a combination of weekly full backups and daily incremental backups.

At some point it no longer makes sense to recover lost files and backups are discarded.

_Archives._ You need to be able to prove you had files at a specific time. This isn't usually an issue
for home users but it can come up in business and academic settings. A typical archive is produced monthly
and retained forever.

In all cases the backup media plays a big role. How much can you store? How fast can you do it? Will you
lose it if you have a power spike? House fire? Flood? Earthquake? Plague of locusts? Does it require manual intervention? For what it's worth cloud storage sounds promising but the bottleneck is the time it takes
to upload and download data. Archival-quality CD-Rs and DVD-Rs are known to be readable for decades but
it will probably require having someone physically swap media. Repeatedly.  (N.B., the bargain
basement media is _not_ suitable for backups.) Taiyo Yuden is highly recommended.

You have to decide what works best for you.

### What You Should Not Backup ###

Not everything should be backed up. The key questions to ask yourself are "can I get this elsewhere?"
and "how often does this change?".

Your operating system and applications, for instance, should be reinstalled from trusted media
instead of backups. This saves a lot of space and avoids reinstalling software that's been compromised
by an attacker.

Your purchased music and video should also not be covered by routine backups. You can repurchase or
re-rip it if you kept the original media. (It does make sense to make archive copies of MP3s.)

You do want to back up your photographs - but do it intelligently. Make a few archive copies of the
existing photographs and only make routine backups of the most recent ones. This can save many gigabytes
of backup space.

A more subtle issue is encrypted home directories. You want to back up the _encrypted_ files, not
the unencrypted files that a logged-in user sees, but if you're not careful you'll back up the
unencrypted files and defeat the purpose of the encryption if someone somehow gets a copy of your
backups.

Finally, many applications require their own backup and restoration programs. Databases are a classic
example - you never want to back up and restore the underlying files, esp. from a live database. You
should use the backup and restore applications provided with the database.

Subversion databases are another example of an application with its own backup and restore tools.

### Caches ###

You do not want to back up caches - by definition you can get this information elsewhere. With tar,
at least, you can identify cache directories with a file named CACHEDIR.TAG and it will not be
backed up.

### Layout ###

The daily/weekly backups are laid out in the following directory structure:

**_/backups/weekly_ - the top-most level. This is in contrast to _backups/monthly_.** _system_ - system name. This allows multiple systems to use the same NFS-mounted /backup location.
**_filesystem_ - the filesystem being backed up. E.g., "home" for user home directories (/home)** _week_ - the current date using week number instead of month and day. This feels odd at first but
it /greatly/ simplifies the scripts and the conventional date is available on the actual files.
**_system-filesystem-user-date.tar.gz_ - the actual compressed tarball.**

Put everything together and a typical filename is /backups\/weekly\/oberon\/home\/2013w21\/oberon-home-bgiles-2013-05-31.tar.gz/.

The backup files include a "label" so they can be identified even if the filename is changed.

The monthly backups follow a similar convention.

### Daily Backups ###

This file should be put in /etc/cron.daily/backups-daily.

A full backup is performed every Sunday, followed by a daily incremental backup.

```
#!/bin/bash

PREFIX=/backups/weekly
SYSTEM=`uname -n`
DOW=`/bin/date +%u`
TS=`/bin/date +%Y-%m-%d`
TSWEEKLY=`/bin/date +%Yw%U`

#
# back up user home directories.
#
for USER in bgiles
do
	DIR=${PREFIX}/users/${USER}/home/${TSWEEKLY}
	/usr/bin/install -d ${DIR}

	LABEL=home-${USER}-${SYSTEM}
	BASENAME=${DIR}/${LABEL}

	/bin/tar czf ${BASENAME}-${TS}.tar.gz \
		--listed-incremental=${BASENAME}-${TSWEEKLY}.snar \
		--exclude-tag=NOARCHIVE.TAG \
		--exclude-caches-all \
		--preserve-permissions \
		--sparse \
		--label=${LABEL}-${TS} \
		--one-file-system \
		--directory /${FS}/${USER} .

    /bin/chown -R backup:backup ${DIR}
    /bin/chmod -R o-rwx ${DIR}
done
```

### Weekly Backups ###

There's no need for an explicit weekly backup since the script above will create a full backup every Sunday.
However it's a good time to purge old backups and, if you're a developer, create database backups.

```
#!/bin/bash

PREFIX=/backups/weekly
SYSTEM=`uname -n`
TS=`/bin/date +%Y-%m-%d`

#
# delete any daily/weekly backup over 8 weeks old.
#
for USER in bgiles
do
   DIR=${PREFIX}/users/${USER}
   /usr/bin/find ${DIR} -ctime +54 -exec /bin/rm {} \;
done

#
# back up personal databases
#
for USER in bgiles
do
   DIR=${PREFIX}/users/${USER}/databases
   /usr/bin/install -d ${DIR}
   /bin/chown ${USER} ${DIR}

   export PGUSER=${USER}
   export PGPASSFILE=/home/${USER}/.pgpass
   su -c "/usr/bin/pg_dump --create --format=custom \
      --file ${DIR}/${USER}-${SYSTEM}-${TS}.pgsql" ${USER}
done
```

### Monthly Archives ###

This file should be put in /etc/cron.monthly/backups-monthly.

A full backup is performed on the first of every month. It is similar to the daily/weekly backups
but should never be purged.

```
#!/bin/bash

PREFIX=/backups/monthly
SYSTEM=`uname -n`
TS=`/bin/date +%Y-%m-%d`

#
# back up user home directories.
#
for USER in bgiles
do
	DIR=${PREFIX}/users/${USER}/home/${TS}
	/usr/bin/install -d ${DIR}

	LABEL=home-${USER}-${SYSTEM}-${TS}
	BASENAME=${DIR}/${LABEL}
	/bin/tar czf ${BASENAME}.tar.gz  \
		--index-file ${BASENAME}.idx \
		--exclude-tag=NOARCHIVE.TAG \
		--exclude-caches-all \
		--preserve-permissions \
		--sparse \
		--label=${LABEL} \
		--one-file-system \
		--directory /${FS}/${USER} .

	/bin/gzip ${BASENAME}.idx

	/bin/chown -R backup:backup ${DIR}
	/bin/chmod -R o-rwx ${DIR}
done

#
# back up personal databases
#
for USER in bgiles
do
  DIR=${PREFIX}/users/${USER}/databases
  /usr/bin/install -d ${DIR}
  /bin/chown ${USER} ${DIR}

  export PGUSER=${USER}
  export PGPASSFILE=/home/${USER}/.pgpass
  su -c "/usr/bin/pg_dump --create --format=custom \
     --file=${DIR}/${USER}-${SYSTEM}-${TS}.pgsql ${USER}" ${USER}
done

#
# back up packages.  (should also backup /etc?)
#
DIR=${PREFIX}/${SYSTEM}/packages
/usr/bin/install -d ${DIR}
/usr/bin/dpkg --get-selections | gzip - > ${DIR}/${SYSTEM}-packages-${TS}.txt.g
```
