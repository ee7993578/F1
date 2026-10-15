import { useState, useMemo } from 'react'
import { apiFetch } from '../../../../lib/api'
import { useStudents } from '../../../../hooks/useStudents'
import {
  ArrowRight, ArrowLeft, ChevronDown, Search,
  CheckSquare, Square, Users, GraduationCap,
  RefreshCw, Check, AlertCircle, X, MoveRight
} from 'lucide-react'

// ─── TINY REUSABLE COMPONENTS ─────────────────────────────────────────────────
function NativeSelect({ value, onChange, children, className = '', placeholder }) {
  return (
    <div className="relative">
      <select
        value={value}
        onChange={onChange}
        className={`w-full appearance-none pl-3 pr-8 py-2 text-[13px] rounded-lg border outline-none transition-all cursor-pointer
          bg-white text-slate-800 border-slate-200
          focus:border-blue-400 focus:ring-2 focus:ring-blue-100
          dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]
          dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20 ${className}`}
      >
        {placeholder && <option value="">{placeholder}</option>}
        {children}
      </select>
      <ChevronDown className="absolute right-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
    </div>
  )
}

function SearchInput({ value, onChange, placeholder = 'Search…' }) {
  return (
    <div className="relative">
      <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
      <input
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        className="w-full pl-8 pr-3 py-1.5 text-[12px] rounded-lg border outline-none transition-all
          bg-white text-slate-700 border-slate-200 placeholder-slate-300
          focus:border-blue-400 focus:ring-2 focus:ring-blue-100
          dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]
          dark:placeholder-slate-600 dark:focus:border-indigo-400"
      />
    </div>
  )
}

// ─── STUDENT LIST PANEL ───────────────────────────────────────────────────────
function StudentPanel({
  title,
  accentColor,       // 'blue' | 'violet'
  classId,
  classOptions,
  onClassChange,
  students,
  selectedIds,
  onToggle,
  onSelectAll,
  search,
  onSearch,
  badge,
}) {
  const isBlue = accentColor === 'blue'
  const accent = isBlue
    ? { bar: 'bg-blue-500', ring: 'focus:ring-blue-100 dark:focus:ring-blue-500/20', check: 'bg-blue-500 border-blue-500 dark:bg-blue-500', countBg: 'bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400', headerBg: 'bg-blue-50/60 dark:bg-blue-900/10' }
    : { bar: 'bg-violet-500', ring: 'focus:ring-violet-100 dark:focus:ring-violet-500/20', check: 'bg-violet-500 border-violet-500 dark:bg-violet-500', countBg: 'bg-violet-100 text-violet-700 dark:bg-violet-500/15 dark:text-violet-400', headerBg: 'bg-violet-50/60 dark:bg-violet-900/10' }

  const allSelected = students.length > 0 && students.every(s => selectedIds.includes(s.id))
  const someSelected = students.some(s => selectedIds.includes(s.id))

  return (
    <div className="flex flex-col rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] overflow-hidden bg-white dark:bg-[#1a1f35] shadow-sm">

      {/* Panel Header */}
      <div className={`px-4 py-3 border-b border-slate-200 dark:border-[rgba(99,102,241,0.15)] ${accent.headerBg}`}>
        <div className="flex items-center gap-2 mb-2.5">
          <span className={`w-1 h-4 rounded-full flex-shrink-0 ${accent.bar}`} />
          <span className="text-[13px] font-bold text-slate-700 dark:text-slate-200 flex-1">{title}</span>
          {badge != null && (
            <span className={`text-[11px] font-semibold px-2 py-0.5 rounded-full ${accent.countBg}`}>
              {badge}
            </span>
          )}
        </div>
        {/* Class Selector */}
        <NativeSelect value={classId} onChange={e => onClassChange(e.target.value)} placeholder="-- Select Class / Section --">
          {classOptions.map(c => <option key={c.id} value={c.id}>{c.label}</option>)}
        </NativeSelect>
      </div>

      {/* Search + Select All */}
      {classId && (
        <div className="px-3 py-2.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02] space-y-2">
          <SearchInput value={search} onChange={e => onSearch(e.target.value)} />
          <button
            type="button"
            onClick={() => onSelectAll(!allSelected)}
            className="flex items-center gap-1.5 text-[11px] font-semibold text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-200 transition-colors"
          >
            {allSelected
              ? <CheckSquare className="w-3.5 h-3.5 text-blue-500 dark:text-indigo-400" />
              : someSelected
                ? <Square className="w-3.5 h-3.5 text-blue-400 dark:text-indigo-400/70" />
                : <Square className="w-3.5 h-3.5" />
            }
            {allSelected ? 'Deselect All' : 'Select All'}
            <span className="ml-auto text-slate-400 dark:text-slate-500">{students.length} students</span>
          </button>
        </div>
      )}

      {/* Student List */}
      <div className="flex-1 overflow-y-auto min-h-[180px] max-h-[360px]">
        {!classId ? (
          <div className="flex flex-col items-center justify-center h-40 gap-2 text-slate-400 dark:text-slate-600">
            <GraduationCap className="w-8 h-8 opacity-40" />
            <span className="text-[12px]">Select a class to view students</span>
          </div>
        ) : students.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-40 gap-2 text-slate-400 dark:text-slate-600">
            <Users className="w-8 h-8 opacity-40" />
            <span className="text-[12px]">No students found</span>
          </div>
        ) : (
          <ul className="divide-y divide-slate-100 dark:divide-[rgba(99,102,241,0.08)]">
            {students.map(s => {
              const checked = selectedIds.includes(s.id)
              return (
                <li key={s.id}>
                  <button
                    type="button"
                    onClick={() => onToggle(s.id)}
                    className={`w-full flex items-center gap-3 px-4 py-2.5 text-left transition-colors
                      ${checked
                        ? isBlue
                          ? 'bg-blue-50/70 dark:bg-blue-500/[0.08]'
                          : 'bg-violet-50/70 dark:bg-violet-500/[0.08]'
                        : 'hover:bg-slate-50 dark:hover:bg-white/[0.03]'
                      }`}
                  >
                    {/* Checkbox */}
                    <span className={`w-4 h-4 rounded border-2 flex-shrink-0 flex items-center justify-center transition-all
                      ${checked
                        ? `${accent.check} dark:border-transparent`
                        : 'border-slate-300 dark:border-slate-600'
                      }`}>
                      {checked && (
                        <svg viewBox="0 0 10 10" className="w-2.5 h-2.5">
                          <polyline points="1.5,5 4,7.5 8.5,2" stroke="white" strokeWidth="1.8" fill="none" strokeLinecap="round" strokeLinejoin="round"/>
                        </svg>
                      )}
                    </span>

                    {/* Avatar */}
                    <span className={`w-7 h-7 rounded-full flex-shrink-0 flex items-center justify-center text-[10px] font-bold
                      ${isBlue
                        ? 'bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400'
                        : 'bg-violet-100 text-violet-700 dark:bg-violet-500/15 dark:text-violet-400'
                      }`}>
                      {s.name.split(' ').map(n => n[0]).join('').slice(0,2)}
                    </span>

                    {/* Info */}
                    <span className="flex-1 min-w-0">
                      <span className="block text-[12px] font-semibold text-slate-700 dark:text-slate-200 truncate">{s.name}</span>
                      <span className="block text-[10px] text-slate-400 dark:text-slate-500">{s.adm} · Roll {s.roll}</span>
                    </span>
                  </button>
                </li>
              )
            })}
          </ul>
        )}
      </div>

      {/* Selected count footer */}
      {classId && selectedIds.length > 0 && (
        <div className={`px-4 py-2 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] text-[11px] font-semibold ${accent.countBg} flex items-center gap-1.5`}>
          <Check className="w-3 h-3" />
          {selectedIds.length} selected
        </div>
      )}
    </div>
  )
}

// ─── TRANSFER ARROWS (Desktop center column) ──────────────────────────────────
function TransferButtons({ onMoveRight, onMoveLeft, rightCount, leftCount }) {
  return (
    <div className="hidden lg:flex flex-col items-center justify-center gap-3 pt-24">
      <button
        type="button"
        onClick={onMoveRight}
        disabled={rightCount === 0}
        title="Move selected to target class"
        className={`w-10 h-10 rounded-xl border-2 flex items-center justify-center transition-all shadow-sm
          ${rightCount > 0
            ? 'bg-blue-500 border-blue-500 text-white hover:bg-blue-600 active:scale-95 shadow-blue-200 dark:shadow-blue-500/20'
            : 'bg-slate-100 border-slate-200 text-slate-300 dark:bg-slate-800 dark:border-slate-700 dark:text-slate-600 cursor-not-allowed'
          }`}
      >
        <ArrowRight className="w-4 h-4" />
      </button>
      {rightCount > 0 && (
        <span className="text-[10px] font-bold text-blue-600 dark:text-blue-400">{rightCount}</span>
      )}
      <button
        type="button"
        onClick={onMoveLeft}
        disabled={leftCount === 0}
        title="Move selected back to source class"
        className={`w-10 h-10 rounded-xl border-2 flex items-center justify-center transition-all shadow-sm
          ${leftCount > 0
            ? 'bg-violet-500 border-violet-500 text-white hover:bg-violet-600 active:scale-95 shadow-violet-200 dark:shadow-violet-500/20'
            : 'bg-slate-100 border-slate-200 text-slate-300 dark:bg-slate-800 dark:border-slate-700 dark:text-slate-600 cursor-not-allowed'
          }`}
      >
        <ArrowLeft className="w-4 h-4" />
      </button>
      {leftCount > 0 && (
        <span className="text-[10px] font-bold text-violet-600 dark:text-violet-400">{leftCount}</span>
      )}
    </div>
  )
}

// ─── MOBILE TRANSFER BAR ──────────────────────────────────────────────────────
function MobileTransferBar({ onMoveRight, onMoveLeft, rightCount, leftCount }) {
  return (
    <div className="flex lg:hidden gap-3 justify-center py-1">
      <button
        type="button"
        onClick={onMoveRight}
        disabled={rightCount === 0}
        className={`flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold border-2 transition-all active:scale-95
          ${rightCount > 0
            ? 'bg-blue-500 border-blue-500 text-white shadow-sm shadow-blue-200 dark:shadow-blue-500/20'
            : 'bg-slate-100 border-slate-200 text-slate-300 dark:bg-slate-800 dark:border-slate-700 dark:text-slate-600 cursor-not-allowed'
          }`}
      >
        Move to Target <ArrowRight className="w-4 h-4" />
        {rightCount > 0 && <span className="bg-white/30 text-white px-1.5 py-0.5 rounded-full text-[10px] font-bold">{rightCount}</span>}
      </button>
      <button
        type="button"
        onClick={onMoveLeft}
        disabled={leftCount === 0}
        className={`flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold border-2 transition-all active:scale-95
          ${leftCount > 0
            ? 'bg-violet-500 border-violet-500 text-white shadow-sm shadow-violet-200 dark:shadow-violet-500/20'
            : 'bg-slate-100 border-slate-200 text-slate-300 dark:bg-slate-800 dark:border-slate-700 dark:text-slate-600 cursor-not-allowed'
          }`}
      >
        <ArrowLeft className="w-4 h-4" /> Move Back
        {leftCount > 0 && <span className="bg-white/30 text-white px-1.5 py-0.5 rounded-full text-[10px] font-bold">{leftCount}</span>}
      </button>
    </div>
  )
}

// ─── SUCCESS TOAST ────────────────────────────────────────────────────────────
function SuccessToast({ message, onClose }) {
  return (
    <div className="fixed bottom-6 left-1/2 -translate-x-1/2 z-50 flex items-center gap-3 px-5 py-3 rounded-2xl shadow-xl
      bg-emerald-600 text-white text-[13px] font-semibold min-w-[280px] max-w-[90vw]"
      style={{ animation: 'slideUp 0.3s ease' }}
    >
      <Check className="w-4 h-4 flex-shrink-0" />
      <span className="flex-1">{message}</span>
      <button onClick={onClose} className="ml-2 opacity-70 hover:opacity-100 transition-opacity">
        <X className="w-4 h-4" />
      </button>
      <style>{`@keyframes slideUp { from { opacity:0; transform:translateX(-50%) translateY(16px) } to { opacity:1; transform:translateX(-50%) translateY(0) } }`}</style>
    </div>
  )
}

// ─── MAIN PAGE ────────────────────────────────────────────────────────────────
export default function NewClassAllotment() {
  const { data: liveStudentData } = useStudents()
  const classSectionOptions = useMemo(() => (liveStudentData?.sections || []).map(row => ({
    id: String(row.sectionId),
    classId: row.classId,
    className: row.className || '',
    sectionName: row.sectionName || '',
    label: [row.className, row.sectionName].filter(Boolean).join(' – '),
  })), [liveStudentData])
  const liveStudentsForClass = classId => {
    const option = classSectionOptions.find(item => item.id === String(classId))
    return (liveStudentData?.students || []).filter(row => !option || (String(row.classId) === String(option.classId) && String(row.sectionId) === String(option.id))).map(row => ({ ...row, id: row.uid || row.studentId, name: row.name, adm: row.registrationNo || '', roll: row.rollNo || '' }))
  }
  // Source panel state
  const [srcClass, setSrcClass]           = useState('')
  const [srcStudents, setSrcStudents]     = useState([])
  const [srcSelected, setSrcSelected]     = useState([])
  const [srcSearch, setSrcSearch]         = useState('')

  // Target panel state
  const [tgtClass, setTgtClass]           = useState('')
  const [tgtStudents, setTgtStudents]     = useState([])
  const [tgtSelected, setTgtSelected]     = useState([])
  const [tgtSearch, setTgtSearch]         = useState('')

  // UI state
  const [errors, setErrors]               = useState({})
  const [toast, setToast]                 = useState(null)
  const currentSession = liveStudentData?.sessions?.find(item => String(item.status).toLowerCase() === 'default')?.session || liveStudentData?.sessions?.[0]?.session || ''

  // ── Helpers ────────────────────────────────────────────────────────────────
  const showToast = (msg) => {
    setToast(msg)
    setTimeout(() => setToast(null), 3500)
  }

  const handleSrcClassChange = (id) => {
    setSrcClass(id)
    setSrcStudents(id ? liveStudentsForClass(id) : [])
    setSrcSelected([])
    setSrcSearch('')
    setErrors(p => ({ ...p, srcClass: undefined }))
  }

  const handleTgtClassChange = (id) => {
    setTgtClass(id)
    setTgtStudents(id ? liveStudentsForClass(id) : [])
    setTgtSelected([])
    setTgtSearch('')
    setErrors(p => ({ ...p, tgtClass: undefined }))
  }

  // Toggle single student selection in source
  const toggleSrc = (id) =>
    setSrcSelected(p => p.includes(id) ? p.filter(x => x !== id) : [...p, id])

  // Toggle single student selection in target (for moving back)
  const toggleTgt = (id) =>
    setTgtSelected(p => p.includes(id) ? p.filter(x => x !== id) : [...p, id])

  // Select all/none in source
  const selectAllSrc = (all) => setSrcSelected(all ? srcFiltered.map(s => s.id) : [])

  // Select all/none in target
  const selectAllTgt = (all) => setTgtSelected(all ? tgtFiltered.map(s => s.id) : [])

  // Move selected from source → target
  const moveRight = () => {
    const err = {}
    if (!srcClass) err.srcClass = 'Select source class'
    if (!tgtClass) err.tgtClass = 'Select target class'
    if (srcClass && tgtClass && srcClass === tgtClass) err.tgtClass = 'Source & target cannot be same'
    if (srcSelected.length === 0) err.noSelection = 'Select at least one student'
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    const moving = srcStudents.filter(s => srcSelected.includes(s.id))
    setSrcStudents(p => p.filter(s => !srcSelected.includes(s.id)))
    setSrcSelected([])
    setTgtStudents(p => [...p, ...moving])
    showToast(`${moving.length} student${moving.length > 1 ? 's' : ''} moved to ${classSectionOptions.find(c => c.id === tgtClass)?.label || 'target section'}`)
  }

  // Move selected from target → source (move back)
  const moveLeft = () => {
    if (tgtSelected.length === 0) { setErrors({ noSelection: 'Select students from target panel to move back' }); return }
    setErrors({})
    const moving = tgtStudents.filter(s => tgtSelected.includes(s.id))
    setTgtStudents(p => p.filter(s => !tgtSelected.includes(s.id)))
    setTgtSelected([])
    setSrcStudents(p => [...p, ...moving])
    showToast(`${moving.length} student${moving.length > 1 ? 's' : ''} moved back to source`)
  }

  // Validate + Save
  const handleSave = async () => {
    const err = {}
    if (!srcClass) err.srcClass = 'Select source class'
    if (!tgtClass) err.tgtClass = 'Select target class'
    if (srcClass && tgtClass && srcClass === tgtClass) err.tgtClass = 'Source & target cannot be same'
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    if (!currentSession) { setErrors({ session: 'No academic session is configured' }); return }
    const originalTargetIds = new Set(liveStudentsForClass(tgtClass).map(student => String(student.id)))
    const movedUids = tgtStudents.filter(student => !originalTargetIds.has(String(student.id))).map(student => String(student.uid || student.id))
    if (!movedUids.length) { showToast('No students were moved'); return }
    try {
      const result = await apiFetch('/students/class-allotment', { method: 'PUT', body: JSON.stringify({ sectionId: Number(tgtClass), session: currentSession, studentUids: movedUids }) })
      showToast(result.message || 'Class allotment saved')
    } catch (error) { showToast(error.message || 'Class allotment failed'); }
  }

  const handleReset = () => {
    setSrcClass(''); setSrcStudents([]); setSrcSelected([]); setSrcSearch('')
    setTgtClass(''); setTgtStudents([]); setTgtSelected([]); setTgtSearch('')
    setErrors({})
  }

  // Filtered lists (search)
  const srcFiltered = useMemo(() =>
    srcStudents.filter(s => s.name.toLowerCase().includes(srcSearch.toLowerCase()) || s.adm.toLowerCase().includes(srcSearch.toLowerCase()))
  , [srcStudents, srcSearch])

  const tgtFiltered = useMemo(() =>
    tgtStudents.filter(s => s.name.toLowerCase().includes(tgtSearch.toLowerCase()) || s.adm.toLowerCase().includes(tgtSearch.toLowerCase()))
  , [tgtStudents, tgtSearch])

  return (
    <div className="space-y-4 pb-8 page-animate">

      {/* Page Title */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
        <div>
          <h1 className="font-display text-[20px] font-bold text-slate-800 dark:text-slate-100">
            New Student Class Section Allotment
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Select source &amp; target class, pick students, then click Change.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <span className="hidden sm:flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-slate-100 dark:bg-slate-800 text-[12px] text-slate-500 dark:text-slate-400 font-medium">
            <MoveRight className="w-3.5 h-3.5" />
            Registration → Class Allotment
          </span>
        </div>
      </div>

      {/* Global error banner */}
      {errors.noSelection && (
        <div className="flex items-center gap-2.5 px-4 py-3 rounded-xl border border-rose-200 bg-rose-50 dark:border-rose-500/20 dark:bg-rose-500/[0.07] text-rose-700 dark:text-rose-400 text-[13px] font-medium">
          <AlertCircle className="w-4 h-4 flex-shrink-0" />
          {errors.noSelection}
        </div>
      )}

      {/* ── Main Card ──────────────────────────────────────────────────────── */}
      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

        {/* Card Header */}
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <GraduationCap className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Class Section Allotment</span>
          <div className="flex items-center gap-2 text-[12px] font-medium text-slate-500 dark:text-slate-400">
            <span className="flex items-center gap-1">
              <span className="w-2 h-2 rounded-full bg-blue-400" /> Source
            </span>
            <ArrowRight className="w-3 h-3" />
            <span className="flex items-center gap-1">
              <span className="w-2 h-2 rounded-full bg-violet-400" /> Target
            </span>
          </div>
        </div>

        {/* Panels */}
        <div className="p-4 sm:p-5">

          {/* Desktop: 3-column grid | Mobile: stack */}
          <div className="grid grid-cols-1 lg:grid-cols-[1fr_60px_1fr] gap-4">

            {/* SOURCE PANEL */}
            <div>
              {errors.srcClass && (
                <p className="flex items-center gap-1 text-[11px] text-rose-500 mb-1.5">
                  <AlertCircle className="w-3 h-3" />{errors.srcClass}
                </p>
              )}
              <StudentPanel
                title="Source Class"
                accentColor="blue"
                classId={srcClass}
                classOptions={classSectionOptions}
                onClassChange={handleSrcClassChange}
                students={srcFiltered}
                selectedIds={srcSelected}
                onToggle={toggleSrc}
                onSelectAll={selectAllSrc}
                search={srcSearch}
                onSearch={setSrcSearch}
                badge={srcSelected.length > 0 ? `${srcSelected.length} selected` : srcStudents.length > 0 ? `${srcStudents.length} students` : null}
              />
            </div>

            {/* Desktop Transfer Arrows (center column) */}
            <TransferButtons
              onMoveRight={moveRight}
              onMoveLeft={moveLeft}
              rightCount={srcSelected.length}
              leftCount={tgtSelected.length}
            />

            {/* Mobile Transfer Bar */}
            <div className="lg:hidden">
              <MobileTransferBar
                onMoveRight={moveRight}
                onMoveLeft={moveLeft}
                rightCount={srcSelected.length}
                leftCount={tgtSelected.length}
              />
            </div>

            {/* TARGET PANEL */}
            <div>
              {errors.tgtClass && (
                <p className="flex items-center gap-1 text-[11px] text-rose-500 mb-1.5">
                  <AlertCircle className="w-3 h-3" />{errors.tgtClass}
                </p>
              )}
              <StudentPanel
                title="Target Class"
                accentColor="violet"
                classId={tgtClass}
                classOptions={classSectionOptions}
                onClassChange={handleTgtClassChange}
                students={tgtFiltered}
                selectedIds={tgtSelected}
                onToggle={toggleTgt}
                onSelectAll={selectAllTgt}
                search={tgtSearch}
                onSearch={setTgtSearch}
                badge={tgtSelected.length > 0 ? `${tgtSelected.length} selected` : tgtStudents.length > 0 ? `${tgtStudents.length} students` : null}
              />
            </div>

          </div>
        </div>

        {/* Footer / Action Bar */}
        <div className="flex flex-col sm:flex-row items-center justify-between gap-3 px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
          <p className="text-[12px] text-slate-400 dark:text-slate-500 text-center sm:text-left">
            Select students from source, click arrow to move, then click <strong className="text-slate-600 dark:text-slate-300">Change</strong> to save.
          </p>
          <div className="flex gap-2 w-full sm:w-auto">
            <button
              type="button"
              onClick={handleReset}
              className="flex-1 sm:flex-none flex items-center justify-center gap-1.5 px-4 py-2 rounded-xl text-[13px] font-semibold transition-colors
                bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700"
            >
              <RefreshCw className="w-3.5 h-3.5" /> Reset
            </button>
            <button
              type="button"
              onClick={handleSave}
              className="flex-1 sm:flex-none flex items-center justify-center gap-1.5 px-6 py-2 rounded-xl text-[13px] font-semibold text-white transition-all active:scale-95
                bg-blue-600 hover:bg-blue-700 shadow-lg shadow-blue-500/20
                dark:bg-indigo-600 dark:hover:bg-indigo-700 dark:shadow-indigo-500/20"
            >
              <Check className="w-4 h-4" /> Change
            </button>
          </div>
        </div>
      </div>

      {/* Summary card — visible after students moved */}
      {tgtClass && tgtStudents.length > 0 && (
        <div className="rounded-2xl border border-emerald-200 dark:border-emerald-500/20 bg-emerald-50/60 dark:bg-emerald-500/[0.05] px-5 py-4 flex flex-col sm:flex-row sm:items-center gap-2">
          <Check className="w-4 h-4 text-emerald-600 dark:text-emerald-400 flex-shrink-0" />
          <p className="text-[13px] text-emerald-800 dark:text-emerald-300 font-medium flex-1">
            <strong>{tgtStudents.length}</strong> student{tgtStudents.length > 1 ? 's' : ''} currently in <strong>{classSectionOptions.find(c => c.id === tgtClass)?.label}</strong>. Click <em>Change</em> to confirm and save.
          </p>
        </div>
      )}

      {/* Toast */}
      {toast && <SuccessToast message={toast} onClose={() => setToast(null)} />}
    </div>
  )
}
