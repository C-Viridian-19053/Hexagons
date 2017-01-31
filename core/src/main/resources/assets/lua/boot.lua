print "Running Lua bootstrap code"

local gameutil = {}
local patternQueue = {}

function patternQueue:shuffle()
    for i = #self, 2, -1 do
        local r = game.random(i - 1) + 1
        self[i], self[r] = self[r], self[i]
    end
end

function patternQueue:addNext()
    if self.at >= #self then
        self.at = 0
    end
    self.at = self.at + 1
    self[self.at]()
end

function gameutil.newPatternQueue(queue)
    local unpacked = {at = 0}
    local n = 1
    for _, v in ipairs(queue) do
        for i = 1, v.weight do
            unpacked[n] = v.pattern
            n = n + 1
        end
    end

    if #unpacked < 1 then
        error("Pattern queue must contain at least 1 element with weight > 0!")
    end

    return setmetatable(unpacked, {__index = patternQueue})
end

function prepareEnv(name)
    local env = {
        assert = assert,
        error = error,
        getmetatable = function(t)
            if type(t) == "string" then
                return nil
            end
            return getmetatable(t)
        end,
        ipairs = ipairs,
        load = load,
        next = next,
        pairs = pairs,
        pcall = pcall,
        print = function(v)
            print("Map." .. tostring(name) .. ": " .. tostring(v))
        end,
        rawequal = rawequal,
        rawget = rawget,
        rawlen = rawlen,
        rawset = rawset,
        select = select,
        setmetatable = setmetatable,
        tonumber = tonumber,
        tostring = tostring,
        type = type,
        _VERSION = _VERSION,
        xpcall = xpcall,
        coroutine = nil,
        string = {
            byte = string.byte,
            char = string.char,
            dump = string.dump,
            find = string.find,
            format = string.format,
            gmatch = string.gmatch,
            gsub = string.gsub,
            len = string.len,
            lower = string.lower,
            match = string.match,
            rep = string.rep,
            reverse = string.reverse,
            sub = string.sub,
            upper = string.upper
        },
        table = {
            concat = table.concat,
            insert = table.insert,
            pack = table.pack,
            remove = table.remove,
            sort = table.sort,
            unpack = table.unpack
        },
        math = {
            abs = math.abs,
            acos = math.acos,
            asin = math.asin,
            atan = math.atan,
            atan2 = math.atan2,
            ceil = math.ceil,
            cos = math.cos,
            cosh = math.cosh,
            deg = math.deg,
            exp = math.exp,
            floor = math.floor,
            fmod = math.fmod,
            frexp = math.frexp,
            huge = math.huge,
            ldexp = math.ldexp,
            log = math.log,
            max = math.max,
            min = math.min,
            modf = math.modf,
            pi = math.pi,
            pow = math.pow,
            rad = math.rad,
            random = game.random, --TODO: Check compat
            --randomseed = math.randomseed,
            sin = math.sin,
            sinh = math.sinh,
            sqrt = math.sqrt,
            tan = math.tan,
            tanh = math.tanh
        },
        bit32 = {
            arshift = bit32.arshift,
            band = bit32.band,
            bnot = bit32.bnot,
            bor = bit32.bor,
            btest = bit32.btest,
            bxor = bit32.bxor,
            extract = bit32.extract,
            replace = bit32.replace,
            lrotate = bit32.lrotate,
            lshift = bit32.lshift,
            rrotate = bit32.rrotate,
            rshift = bit32.rshift
        },
        os = {

        },
        debug = debug and {
            getinfo = function(...)
                local result = debug.getinfo(...)
                if result then
                    return {
                        source = result.source,
                        short_src = result.short_src,
                        linedefined = result.linedefined,
                        lastlinedefined = result.lastlinedefined,
                        what = result.what,
                        currentline = result.currentline,
                        nups = result.nups,
                        nparams = result.nparams,
                        isvararg = result.isvararg,
                        name = result.name,
                        namewhat = result.namewhat,
                        istailcall = result.istailcall
                    }
                end
            end,
            traceback = debug.traceback
        },
        game = {
            newPatternQueue = gameutil.newPatternQueue,
            randomParam = game.randomParam,
            random = game.random,
            getHalfSides = game.getHalfSides
        },
        standardPattern = {
            alternatingBarrage = standardPattern.alternatingBarrage,
            mirrorSpiral = standardPattern.mirrorSpiral,
            barrageSpiral = standardPattern.barrageSpiral,
            inverseBarrage = standardPattern.inverseBarrage,
            tunnel = standardPattern.tunnel
        }
    }

    local wenv = {}
    env._G = wenv

    return setmetatable(wenv, {__index = env}), wenv
end
