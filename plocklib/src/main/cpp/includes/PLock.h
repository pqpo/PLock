//
// Created by pqpo on 2018/4/28.
//

#ifndef PROCESSLOCK_PLOCK_H
#define PROCESSLOCK_PLOCK_H

#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sstream>
#include <unistd.h>


class PLock {

    public:
        PLock(char* file);
        ~PLock();
        bool writeLock();
        bool readLock();
        bool tryWriteLock();
        bool tryReadLock();
        bool unlock();

    private:
        char* file;
        int fd = -1;
        void init_flock(flock *lock, short type, short whence, off_t start, off_t len);
        void open_file();
        void close_file();

};


#endif //PROCESSLOCK_PLOCK_H
