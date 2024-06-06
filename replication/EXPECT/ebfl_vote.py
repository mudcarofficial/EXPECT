import os
import re
import openpyxl

def vote(version):
    votes = {}
    with open("THE PATH OF COVERAGE FILE EXTRACTED BY tools\CollectCoverage.java", "r") as f:
        file_contents = f.readlines()
        i = 0
        while i < len(file_contents):
            lineindicator = file_contents[i].strip() + "_" + file_contents[i + 1].strip()
            votes[lineindicator] = 0
            i += 3

    pattern1 = r'^Begin:'
    pattern2 = r'^End:'
    files = os.listdir("PATH TO SAVE THE EXCEPTION TRIGGER STREAMS IN ERRONEOUS EXECUTIONS")
    for filename in files:
        currvotes = set()
        exceptions_ends_a = []
        exceptions_ends_b = []
        exceptions_b = []
        path_a = os.path.join("PATH TO SAVE THE EXCEPTION TRIGGER STREAMS IN CORRECT EXECUTIONS", filename)
        path_b = os.path.join("PATH TO SAVE THE EXCEPTION TRIGGER STREAMS IN ERRONEOUS EXECUTIONS", filename)
        with open(path_b, 'r', encoding='utf-16le') as file_b:
            lines_b = file_b.readlines()
            for i in range(len(lines_b)):
                if re.match(pattern2, lines_b[i]):
                    exceptions_ends_b.append(lines_b[i])
                    exceptions_b.append(lines_b[i])
                elif re.match(pattern1, lines_b[i]):
                    exceptions_b.append(lines_b[i])
        with open(path_a, 'r', encoding='utf-16le') as file_a:
            lines_a = file_a.readlines()
            for i in range(len(lines_a)):
                if re.match(pattern2, lines_a[i]):
                    exceptions_ends_a.append(lines_a[i])
                    if len(exceptions_ends_a) > len(exceptions_ends_b):
                        break

        if(len(exceptions_ends_a) == 0 and len(exceptions_ends_b) == 0):
            continue
        elif(len(exceptions_ends_a) == 0):
            with open(path_b, 'r', encoding='utf-16le') as file_b:
                lines_b = file_b.readlines()
                for i in range(len(lines_b)):
                    if re.match(pattern2, lines_b[i]) or re.match(pattern1, lines_b[i]):
                        break
                    currvotes.add(lines_b[i][:-1])
            for statement in currvotes:
                votes[statement] += 1
            continue
        elif(len(exceptions_ends_b) == 0):
            with open(path_b, 'r', encoding='utf-16le') as file_b:
                lines_b = file_b.readlines()
                for i in range(len(lines_b)):
                    if re.match(pattern2, lines_b[i]) or re.match(pattern1, lines_b[i]):
                        continue
                    currvotes.add(lines_b[i][:-1])
            for statement in currvotes:
                votes[statement] += 1
            continue

        diff = 0
        difftype = "none"
        i = 0
        while i < min(len(exceptions_ends_a), len(exceptions_ends_b)):
            if(exceptions_ends_a[i] != exceptions_ends_b[i]):
                diff = i + 1
                difftype = "normal"
                break
            i += 1
        if difftype == "none":
            if i < len(exceptions_ends_a):
                diff = i
                difftype = "short"
            elif i < len(exceptions_ends_b):
                diff = i
                difftype = "long"
            else:
                continue

        index = 0
        endcount = 0
        while index < len(exceptions_b):
            if re.match(pattern2, exceptions_b[index]):
                endcount += 1
                if endcount == diff:
                    break
            index += 1
        aindex = index - 1
        inside = 0
        while aindex >= 0:
            if re.match(pattern1, exceptions_b[aindex]) and inside == 0:
                break
            elif re.match(pattern1, exceptions_b[aindex]):
                inside -= 1
            elif re.match(pattern2, exceptions_b[aindex]):
                inside += 1
            aindex -= 1
        aindex -= 1
        while aindex >= 0:
            if re.match(pattern2, exceptions_b[aindex]):
                break
            aindex -= 1
        aindex += 1
        index += 1
        findex = 0
        if difftype == "long":
            endcount = 0
            while findex < len(exceptions_b):
                if re.match(pattern2, exceptions_b[findex]):
                    endcount += 1
                    if endcount == diff + 1:
                        break
                findex += 1
        findex += 1

        count = 0
        with open(path_b, 'r', encoding='utf-16le') as file_b:
            lines_b = file_b.readlines()
            for i in range(len(lines_b)):
                if re.match(pattern2, lines_b[i]) or re.match(pattern1, lines_b[i]):
                    count += 1
                elif count >= aindex and count < index:
                    currvotes.add(lines_b[i][:-1])
                elif count >= index and difftype == "short":
                    currvotes.add(lines_b[i][:-1])
                elif count >= index and difftype == "long" and count < findex:
                    currvotes.add(lines_b[i][:-1])
        for statement in currvotes:
            votes[statement] += 1

    listvotes = list(votes.items())
    sortvotes = sorted(listvotes, key=lambda x: x[1], reverse=True)
    workbook = openpyxl.Workbook()
    worksheet = workbook.active
    i = 0
    while i < len(sortvotes):
        r = sortvotes[i]
        row = []
        row.append(r[0])
        row.append(r[1])
        worksheet.append(row)
        i += 1
    workbook.save("PATH TO SAVE THE VOTE-BASED RANKING")

if __name__ == '__main__':
    version = 1
    while version < 601:
        vote(version)
        version += 1