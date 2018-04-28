//
// Created by pqpo on 2018/4/28.
//

#include "includes/PLock.h"

PLock::PLock(char* file) : file(file){
    open_file();
}

PLock::~PLock() {
    close_file();
}

void PLock::open_file() {
    if(fd >= 0) {
        return;
    }
    fd = open(file, O_RDWR|O_CREAT|O_APPEND, S_IRUSR|S_IWUSR|S_IRGRP|S_IROTH);
}

void PLock::close_file() {
    if(fd >= 0) {
        close(fd);
        fd = -1;
    }
}

void PLock::init_flock(flock *lock, short type, short whence, off_t start, off_t len) {
    if (lock == nullptr)
        return;
    lock->l_type = type;
    lock->l_whence = whence;
    lock->l_start = start;
    lock->l_len = len;
}

bool PLock::readLock() {
    if (fd < 0) {
        return false;
    }
    struct flock lock;
    init_flock(&lock, F_RDLCK, SEEK_SET, 0, 0);
    return fcntl(fd, F_SETLKW, &lock) == 0;
}

bool PLock::writeLock() {
    if (fd < 0) {
        return false;
    }
    struct flock lock;
    init_flock(&lock, F_WRLCK, SEEK_SET, 0, 0);
    return fcntl(fd, F_SETLKW, &lock) == 0;
}

bool PLock::tryReadLock() {
    if (fd < 0) {
        return false;
    }
    struct flock lock;
    init_flock(&lock, F_RDLCK, SEEK_SET, 0, 0);
    return fcntl(fd, F_SETLK, &lock) == 0;
}

bool PLock::tryWriteLock() {
    if (fd < 0) {
        return false;
    }
    struct flock lock;
    init_flock(&lock, F_WRLCK, SEEK_SET, 0, 0);
    return fcntl(fd, F_SETLK, &lock) == 0;
}

bool PLock::unlock() {
    if (fd < 0) {
        return false;
    }
    struct flock lock;
    init_flock(&lock, F_UNLCK, SEEK_SET, 0, 0);
    return fcntl(fd, F_SETLKW, &lock) == 0;
}





