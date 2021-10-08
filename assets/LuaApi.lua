function string:split(delimiter) --출처: http://november11tech.tistory.com/84 [Mr.november11]
    local result = {};
    local from = 1;
    local delim_from, delim_to = string.find(self, delimiter, from);
    while delim_from do
        table.insert(result, string.sub(self, from , delim_from-1));
        from = delim_to + 1;
        delim_from, delim_to = string.find(self, delimiter, from);
    end
    table.insert(result, string.sub(self, from));
    return result;
end