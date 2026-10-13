/**
 * VirtualClassReport.jsx
 * Folder: src/pages/Reports/Configuration/VirtualClassReport.jsx
 *
 * Converts legacy ASPX VirtualClassReport page to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Session → Class → Section → Subject → Faculty cascading dropdowns
 *  - Submit + Export Excel buttons
 *  - GridView1: Virtual class list with S.No, Virtual Class, Class, Faculty, Student Count (link), Subject, Delete
 *  - GridView2: Student list modal (Adm No, Student Name, Class+Section) — triggered by Student Count click
 *  - Delete confirmation modal (Yes / No)
 *  - Mobile: cards layout with expandable rows, bottom drawers for filters
 *  - Desktop: dense ERP-style table
 *  - Dark/light theme via Tailwind dark: variants
 */

import { useState, useMemo, useCallback } from 'react'
import {
  Filter, RefreshCw, Eye, AlertCircle, X, Check, Loader2,
  ChevronDown, ChevronRight, SlidersHorizontal, Search,
  FileSpreadsheet, Users, Trash2, School2, BookOpen,
  UserCheck, Building2, MapPin, GraduationCap, Info,
  ChevronLeft, LayoutList
} from 'lucide-react'

// ─── STATIC DUMMY DATA ────────────────────────────────────────────────────────

const SESSIONS = ['2022-23', '2023-24', '2024-25', '2025-26']

const CLASSES_DATA = {
  '2022-23': ['Class I', 'Class II', 'Class III', 'Class IV', 'Class V', 'Class VI', 'Class VII', 'Class VIII', 'Class IX', 'Class X'],
  '2023-24': ['Class I', 'Class II', 'Class III', 'Class IV', 'Class V', 'Class VI', 'Class VII', 'Class VIII', 'Class IX', 'Class X', 'Class XI', 'Class XII'],
  '2024-25': ['Class I', 'Class II', 'Class III', 'Class IV', 'Class V', 'Class VI', 'Class VII', 'Class VIII', 'Class IX', 'Class X', 'Class XI', 'Class XII'],
  '2025-26': ['Nursery', 'LKG', 'UKG', 'Class I', 'Class II', 'Class III', 'Class IV', 'Class V', 'Class VI', 'Class VII', 'Class VIII', 'Class IX', 'Class X', 'Class XI', 'Class XII'],
}

const SECTIONS_DATA = {
  'Class I':    ['A', 'B', 'C'],
  'Class II':   ['A', 'B'],
  'Class III':  ['A', 'B'],
  'Class IV':   ['A'],
  'Class V':    ['A', 'B'],
  'Class VI':   ['A', 'B', 'C'],
  'Class VII':  ['A', 'B'],
  'Class VIII': ['A'],
  'Class IX':   ['A', 'B'],
  'Class X':    ['A', 'B'],
  'Class XI':   ['A', 'B', 'C'],
  'Class XII':  ['A', 'B'],
  'Nursery':    ['A', 'B'],
  'LKG':        ['A', 'B'],
  'UKG':        ['A', 'B'],
}

const SUBJECTS_DATA = {
  'Class I':    ['English', 'Hindi', 'Mathematics', 'EVS', 'Drawing'],
  'Class II':   ['English', 'Hindi', 'Mathematics', 'EVS', 'Computer'],
  'Class III':  ['English', 'Hindi', 'Mathematics', 'Science', 'Social Science'],
  'Class IV':   ['English', 'Hindi', 'Mathematics', 'Science', 'Social Science'],
  'Class V':    ['English', 'Hindi', 'Mathematics', 'Science', 'Social Science', 'Computer'],
  'Class VI':   ['English', 'Hindi', 'Mathematics', 'Science', 'Social Science', 'Sanskrit', 'Computer'],
  'Class VII':  ['English', 'Hindi', 'Mathematics', 'Science', 'Social Science', 'Sanskrit', 'Computer'],
  'Class VIII': ['English', 'Hindi', 'Mathematics', 'Science', 'Social Science', 'Sanskrit'],
  'Class IX':   ['English', 'Hindi', 'Mathematics', 'Science', 'Social Science'],
  'Class X':    ['English', 'Hindi', 'Mathematics', 'Science', 'Social Science'],
  'Class XI':   ['English', 'Physics', 'Chemistry', 'Mathematics', 'Biology', 'Computer Science', 'Accountancy', 'Business Studies', 'Economics'],
  'Class XII':  ['English', 'Physics', 'Chemistry', 'Mathematics', 'Biology', 'Computer Science', 'Accountancy', 'Business Studies', 'Economics'],
  'Nursery':    ['English', 'Hindi', 'Drawing', 'Activity'],
  'LKG':        ['English', 'Hindi', 'Mathematics', 'Drawing'],
  'UKG':        ['English', 'Hindi', 'Mathematics', 'Drawing', 'EVS'],
}

const FACULTY_DATA = [
  'Mr. Amit Kumar', 'Mrs. Priya Sharma', 'Mr. Rajesh Singh',
  'Mrs. Sunita Verma', 'Mr. Deepak Gupta', 'Mrs. Kavita Yadav',
  'Mr. Vinod Tiwari', 'Mrs. Rekha Dubey', 'Mr. Suresh Pandey',
]

const VIRTUAL_CLASSES_DATA = [
  { VClass_id: 'VC001', v_class: 'VC-Maths-IX-A', classname: 'Class IX - A', facultyname: 'Mr. Deepak Gupta',   studentcount: 42, subject: 'Mathematics' },
  { VClass_id: 'VC002', v_class: 'VC-Science-X-B', classname: 'Class X - B',  facultyname: 'Mrs. Priya Sharma', studentcount: 38, subject: 'Science' },
  { VClass_id: 'VC003', v_class: 'VC-English-VI-A', classname: 'Class VI - A', facultyname: 'Mr. Amit Kumar',   studentcount: 56, subject: 'English' },
  { VClass_id: 'VC004', v_class: 'VC-Hindi-VIII-A', classname: 'Class VIII - A', facultyname: 'Mrs. Sunita Verma', studentcount: 47, subject: 'Hindi' },
  { VClass_id: 'VC005', v_class: 'VC-Physics-XI-B', classname: 'Class XI - B', facultyname: 'Mr. Rajesh Singh',  studentcount: 35, subject: 'Physics' },
  { VClass_id: 'VC006', v_class: 'VC-Chem-XII-A',  classname: 'Class XII - A', facultyname: 'Mrs. Kavita Yadav', studentcount: 30, subject: 'Chemistry' },
  { VClass_id: 'VC007', v_class: 'VC-EVS-III-A',   classname: 'Class III - A', facultyname: 'Mrs. Rekha Dubey',  studentcount: 40, subject: 'EVS' },
  { VClass_id: 'VC008', v_class: 'VC-Comp-VII-B',  classname: 'Class VII - B', facultyname: 'Mr. Vinod Tiwari', studentcount: 44, subject: 'Computer' },
]

const STUDENTS_BY_VCLASS = {
  'VC001': [
    { Registration_NO: 'ADM2024001', studentname: 'Aarav Sharma',    class_name: 'Class IX',  section: 'A' },
    { Registration_NO: 'ADM2024002', studentname: 'Priya Verma',     class_name: 'Class IX',  section: 'A' },
    { Registration_NO: 'ADM2024003', studentname: 'Rohan Gupta',     class_name: 'Class IX',  section: 'A' },
    { Registration_NO: 'ADM2024004', studentname: 'Sneha Singh',     class_name: 'Class IX',  section: 'A' },
    { Registration_NO: 'ADM2024005', studentname: 'Akash Dubey',     class_name: 'Class IX',  section: 'A' },
    { Registration_NO: 'ADM2024006', studentname: 'Pooja Tiwari',    class_name: 'Class IX',  section: 'A' },
  ],
  'VC002': [
    { Registration_NO: 'ADM2024011', studentname: 'Vivek Pandey',    class_name: 'Class X',   section: 'B' },
    { Registration_NO: 'ADM2024012', studentname: 'Nisha Yadav',     class_name: 'Class X',   section: 'B' },
    { Registration_NO: 'ADM2024013', studentname: 'Mohit Saxena',    class_name: 'Class X',   section: 'B' },
    { Registration_NO: 'ADM2024014', studentname: 'Kavya Mishra',    class_name: 'Class X',   section: 'B' },
  ],
  'VC003': [
    { Registration_NO: 'ADM2024021', studentname: 'Arjun Malik',     class_name: 'Class VI',  section: 'A' },
    { Registration_NO: 'ADM2024022', studentname: 'Divya Chauhan',   class_name: 'Class VI',  section: 'A' },
    { Registration_NO: 'ADM2024023', studentname: 'Ravi Joshi',      class_name: 'Class VI',  section: 'A' },
  ],
  'VC004': [
    { Registration_NO: 'ADM2024031', studentname: 'Tanvi Shukla',    class_name: 'Class VIII', section: 'A' },
    { Registration_NO: 'ADM2024032', studentname: 'Harsh Tripathi',  class_name: 'Class VIII', section: 'A' },
  ],
  'VC005': [
    { Registration_NO: 'ADM2024041', studentname: 'Aditya Rao',      class_name: 'Class XI',  section: 'B' },
    { Registration_NO: 'ADM2024042', studentname: 'Shreya Bansal',   class_name: 'Class XI',  section: 'B' },
    { Registration_NO: 'ADM2024043', studentname: 'Nikhil Agarwal',  class_name: 'Class XI',  section: 'B' },
  ],
  'VC006': [
    { Registration_NO: 'ADM2024051', studentname: 'Meera Kapoor',    class_name: 'Class XII', section: 'A' },
    { Registration_NO: 'ADM2024052', studentname: 'Saurabh Lal',     class_name: 'Class XII', section: 'A' },
  ],
  'VC007': [
    { Registration_NO: 'ADM2024061', studentname: 'Ishaan Mehta',    class_name: 'Class III', section: 'A' },
    { Registration_NO: 'ADM2024062', studentname: 'Riya Srivastava', class_name: 'Class III', section: 'A' },
    { Registration_NO: 'ADM2024063', studentname: 'Kartik Nair',     class_name: 'Class III', section: 'A' },
    { Registration_NO: 'ADM2024064', studentname: 'Ananya Das',      class_name: 'Class III', section: 'A' },
  ],
  'VC008': [
    { Registration_NO: 'ADM2024071', studentname: 'Yash Bhatt',      class_name: 'Class VII', section: 'B' },
    { Registration_NO: 'ADM2024072', studentname: 'Sakshi Patel',    class_name: 'Class VII', section: 'B' },
    { Registration_NO: 'ADM2024073', studentname: 'Dev Reddy',       class_name: 'Class VII', section: 'B' },
  ],
}

// ─── HELPERS ──────────────────────────────────────────────────────────────────

const CLASS_COLORS = [
  { fg: '#1d4ed8', bg: '#dbeafe' },
  { fg: '#7c3aed', bg: '#ede9fe' },
  { fg: '#0891b2', bg: '#cffafe' },
  { fg: '#059669', bg: '#d1fae5' },
  { fg: '#d97706', bg: '#fef3c7' },
  { fg: '#dc2626', bg: '#fee2e2' },
  { fg: '#0369a1', bg: '#e0f2fe' },
]
const classColor = (name = '') => CLASS_COLORS[(name.charCodeAt(0) ?? 0) % CLASS_COLORS.length]
const formatAbbr = (name = '') => name.replace('Class ', 'C').slice(0, 3).toUpperCase()

// ─── PRIMITIVES ───────────────────────────────────────────────────────────────

function NativeSelect({ value, onChange, children, placeholder, error, disabled }) {
  return (
    <div className="relative">
      <select
        value={value}
        onChange={onChange}
        disabled={disabled}
        className={`w-full appearance-none pl-3 pr-8 py-2 text-[13px] rounded-lg border outline-none transition-all cursor-pointer
          bg-white text-slate-800
          focus:border-blue-400 focus:ring-2 focus:ring-blue-100
          dark:bg-[#1e2238] dark:text-slate-200 dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
          disabled:opacity-50 disabled:cursor-not-allowed
          ${error ? 'border-rose-400 ring-2 ring-rose-100' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
      >
        {placeholder && <option value="">{placeholder}</option>}
        {children}
      </select>
      <ChevronDown className="absolute right-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
    </div>
  )
}

function Field({ label, error, required, children }) {
  return (
    <div className="flex flex-col gap-1">
      <label className="text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wide">
        {label}{required && <span className="text-rose-500 ml-0.5">*</span>}
      </label>
      {children}
      {error && (
        <p className="flex items-center gap-1 text-[11px] text-rose-500">
          <AlertCircle className="w-3 h-3 flex-shrink-0" />{error}
        </p>
      )}
    </div>
  )
}

function Toast({ message, type = 'success', onClose }) {
  return (
    <div
      className={`fixed bottom-6 left-1/2 -translate-x-1/2 z-[999] flex items-center gap-3 px-5 py-3
        rounded-2xl shadow-xl text-[13px] font-semibold min-w-[260px] max-w-[90vw]
        ${type === 'success' ? 'bg-emerald-600 text-white' : 'bg-rose-600 text-white'}`}
      style={{ animation: 'slideUp .25s ease' }}
    >
      {type === 'success' ? <Check className="w-4 h-4 flex-shrink-0" /> : <AlertCircle className="w-4 h-4 flex-shrink-0" />}
      <span className="flex-1">{message}</span>
      <button onClick={onClose}><X className="w-4 h-4 opacity-75 hover:opacity-100" /></button>
      <style>{`@keyframes slideUp{from{opacity:0;transform:translateX(-50%) translateY(10px)}to{opacity:1;transform:translateX(-50%) translateY(0)}}`}</style>
    </div>
  )
}

// ─── DELETE CONFIRM MODAL ─────────────────────────────────────────────────────

function DeleteModal({ open, onConfirm, onCancel, vcName }) {
  if (!open) return null
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
      <div
        className="bg-white dark:bg-[#1a1f35] rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.25)] shadow-2xl w-full max-w-sm"
        style={{ animation: 'modalIn .2s ease' }}
      >
        <style>{`@keyframes modalIn{from{opacity:0;transform:scale(.93)}to{opacity:1;transform:scale(1)}}`}</style>
        {/* Header */}
        <div className="flex items-center justify-between px-5 py-4 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
          <div className="flex items-center gap-2.5">
            <span className="w-8 h-8 rounded-xl bg-rose-100 dark:bg-rose-500/15 flex items-center justify-center">
              <Trash2 className="w-4 h-4 text-rose-600 dark:text-rose-400" />
            </span>
            <span className="text-[15px] font-bold text-slate-800 dark:text-slate-100">Delete Virtual Class</span>
          </div>
          <button onClick={onCancel} className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400">
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Body */}
        <div className="px-5 py-5">
          <p className="text-[14px] text-slate-600 dark:text-slate-300 text-center leading-relaxed">
            Are you sure you want to delete virtual class<br />
            {vcName && <strong className="text-slate-800 dark:text-slate-100">"{vcName}"</strong>}?<br />
            <span className="text-[12px] text-rose-500 dark:text-rose-400 mt-1 block">This action cannot be undone.</span>
          </p>
        </div>

        {/* Footer */}
        <div className="flex gap-3 px-5 pb-5">
          <button
            onClick={onCancel}
            className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors"
          >
            No, Cancel
          </button>
          <button
            onClick={onConfirm}
            className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-rose-600 text-white hover:bg-rose-700 shadow-md shadow-rose-500/20 transition-all active:scale-95"
          >
            Yes, Delete
          </button>
        </div>
      </div>
    </div>
  )
}

// ─── STUDENT LIST MODAL (GridView2) ──────────────────────────────────────────

function StudentListModal({ open, onClose, students, vcName }) {
  const [search, setSearch] = useState('')
  const filtered = useMemo(() => {
    if (!search) return students
    const q = search.toLowerCase()
    return students.filter(s =>
      s.studentname.toLowerCase().includes(q) ||
      s.Registration_NO.toLowerCase().includes(q) ||
      s.class_name.toLowerCase().includes(q)
    )
  }, [students, search])

  if (!open) return null
  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center bg-black/50 backdrop-blur-sm">
      <div
        className="bg-white dark:bg-[#1a1f35] w-full sm:max-w-2xl sm:mx-4 rounded-t-2xl sm:rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.25)] shadow-2xl flex flex-col"
        style={{ maxHeight: '90vh', animation: 'slideUpModal .25s ease' }}
      >
        <style>{`@keyframes slideUpModal{from{opacity:0;transform:translateY(40px)}to{opacity:1;transform:translateY(0)}}`}</style>

        {/* Drag handle (mobile) */}
        <div className="flex justify-center pt-3 pb-1 sm:hidden">
          <div className="w-10 h-1 rounded-full bg-slate-200 dark:bg-slate-700" />
        </div>

        {/* Header */}
        <div className="flex items-center justify-between px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] flex-shrink-0">
          <div className="flex items-center gap-2.5">
            <span className="w-8 h-8 rounded-xl bg-blue-100 dark:bg-blue-500/15 flex items-center justify-center">
              <Users className="w-4 h-4 text-blue-600 dark:text-blue-400" />
            </span>
            <div>
              <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 leading-tight">Student List</p>
              {vcName && <p className="text-[11px] text-slate-400 dark:text-slate-500">{vcName}</p>}
            </div>
          </div>
          <div className="flex items-center gap-2">
            <span className="px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400">
              {filtered.length} students
            </span>
            <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400">
              <X className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* Search */}
        <div className="px-4 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] flex-shrink-0">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
            <input
              value={search}
              onChange={e => setSearch(e.target.value)}
              placeholder="Search student name or adm no…"
              className="w-full pl-9 pr-8 py-2 text-[12px] rounded-lg border outline-none transition-all
                bg-white text-slate-700 border-slate-200 placeholder-slate-300
                focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]
                dark:placeholder-slate-600 dark:focus:border-indigo-400"
            />
            {search && (
              <button onClick={() => setSearch('')} className="absolute right-2.5 top-1/2 -translate-y-1/2 text-slate-400">
                <X className="w-3.5 h-3.5" />
              </button>
            )}
          </div>
        </div>

        {/* Table / List */}
        <div className="overflow-y-auto flex-1">
          {/* Desktop Table */}
          <div className="hidden sm:block">
            {filtered.length === 0 ? (
              <div className="flex flex-col items-center justify-center py-12 gap-2 text-slate-400">
                <Search className="w-6 h-6 opacity-40" />
                <span className="text-[13px]">No students found</span>
              </div>
            ) : (
              <table className="w-full">
                <thead>
                  <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/70 dark:bg-white/[0.02] sticky top-0">
                    {['S.No.', 'Adm No.', 'Student Name', 'Class & Section'].map((h, i) => (
                      <th key={i} className="px-4 py-2.5 text-left text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap first:w-12 first:text-center">
                        {h}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {filtered.map((s, i) => (
                    <tr key={s.Registration_NO} className="border-b border-slate-50 dark:border-[rgba(99,102,241,0.05)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors">
                      <td className="px-4 py-3 text-center text-[12px] text-slate-400 tabular-nums">{i + 1}</td>
                      <td className="px-4 py-3">
                        <span className="text-[12px] font-mono font-semibold text-blue-600 dark:text-blue-400">{s.Registration_NO}</span>
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-2">
                          <span className="w-7 h-7 rounded-full bg-indigo-100 dark:bg-indigo-500/15 flex items-center justify-center text-[10px] font-bold text-indigo-600 dark:text-indigo-400 flex-shrink-0">
                            {s.studentname.charAt(0)}
                          </span>
                          <span className="text-[13px] font-semibold text-slate-700 dark:text-slate-200">{s.studentname}</span>
                        </div>
                      </td>
                      <td className="px-4 py-3">
                        <span className="text-[12px] text-slate-500 dark:text-slate-400">{s.class_name} – {s.section}</span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>

          {/* Mobile Cards */}
          <div className="sm:hidden p-3 space-y-2">
            {filtered.length === 0 ? (
              <div className="flex flex-col items-center justify-center py-10 gap-2 text-slate-400">
                <Search className="w-6 h-6 opacity-40" />
                <span className="text-[13px]">No students found</span>
              </div>
            ) : (
              filtered.map((s, i) => (
                <div key={s.Registration_NO}
                  className="flex items-center gap-3 rounded-xl border border-slate-100 dark:border-[rgba(99,102,241,0.12)] bg-white dark:bg-[#1e2238] px-3 py-3">
                  <span className="w-8 h-8 rounded-full bg-indigo-100 dark:bg-indigo-500/15 flex items-center justify-center text-[11px] font-bold text-indigo-600 dark:text-indigo-400 flex-shrink-0">
                    {i + 1}
                  </span>
                  <div className="flex-1 min-w-0">
                    <p className="text-[13px] font-bold text-slate-800 dark:text-slate-100 truncate">{s.studentname}</p>
                    <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5">
                      <span className="font-mono text-blue-600 dark:text-blue-400">{s.Registration_NO}</span>
                      &nbsp;·&nbsp;{s.class_name} – {s.section}
                    </p>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        {/* Footer */}
        <div className="px-5 py-3.5 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] flex-shrink-0">
          <button
            onClick={onClose}
            className="w-full py-2.5 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  )
}

// ─── MOBILE FILTER DRAWER ─────────────────────────────────────────────────────

function FilterDrawer({ open, onClose, filters, setFilters, derivedData, onSubmit, loading, errors }) {
  if (!open) return null
  const { classes, sections, subjects } = derivedData

  return (
    <>
      <div className="fixed inset-0 bg-black/40 z-40 backdrop-blur-sm" onClick={onClose} />
      <div
        className="fixed inset-x-0 bottom-0 z-50 rounded-t-2xl bg-white dark:bg-[#1a1f35] border-t border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-2xl"
        style={{ maxHeight: '90vh', overflowY: 'auto', animation: 'drawerUp .25s ease' }}
      >
        <style>{`@keyframes drawerUp{from{transform:translateY(100%)}to{transform:translateY(0)}}`}</style>
        <div className="flex justify-center pt-3 pb-1"><div className="w-10 h-1 rounded-full bg-slate-200 dark:bg-slate-700" /></div>
        <div className="flex items-center justify-between px-5 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
          <div className="flex items-center gap-2">
            <SlidersHorizontal className="w-4 h-4 text-blue-600 dark:text-indigo-400" />
            <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Filters</span>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400">
            <X className="w-4 h-4" />
          </button>
        </div>

        <div className="px-5 py-4 space-y-4">
          <Field label="Session" error={errors.session} required>
            <NativeSelect value={filters.session} onChange={e => setFilters(p => ({ ...p, session: e.target.value, cls: '', section: '', subject: '', faculty: '' }))} placeholder="-- Select Session --" error={errors.session}>
              {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
            </NativeSelect>
          </Field>
          <Field label="Class" error={errors.cls}>
            <NativeSelect value={filters.cls} onChange={e => setFilters(p => ({ ...p, cls: e.target.value, section: '', subject: '' }))} placeholder="-- Select Class --" disabled={!filters.session}>
              {classes.map(c => <option key={c} value={c}>{c}</option>)}
            </NativeSelect>
          </Field>
          <Field label="Section">
            <NativeSelect value={filters.section} onChange={e => setFilters(p => ({ ...p, section: e.target.value }))} placeholder="-- Select Section --" disabled={!filters.cls}>
              {sections.map(s => <option key={s} value={s}>{s}</option>)}
            </NativeSelect>
          </Field>
          <Field label="Subject">
            <NativeSelect value={filters.subject} onChange={e => setFilters(p => ({ ...p, subject: e.target.value }))} placeholder="-- Select Subject --" disabled={!filters.cls}>
              {subjects.map(s => <option key={s} value={s}>{s}</option>)}
            </NativeSelect>
          </Field>
          <Field label="Faculty">
            <NativeSelect value={filters.faculty} onChange={e => setFilters(p => ({ ...p, faculty: e.target.value }))} placeholder="-- Select Faculty --">
              {FACULTY_DATA.map(f => <option key={f} value={f}>{f}</option>)}
            </NativeSelect>
          </Field>
        </div>

        <div className="px-5 pb-6 pt-2 flex gap-3 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
          <button type="button" onClick={onClose}
            className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300 transition-colors">
            Cancel
          </button>
          <button type="button" onClick={() => { onSubmit(); onClose() }} disabled={loading}
            className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold text-white bg-blue-600 hover:bg-blue-700 dark:bg-indigo-600 dark:hover:bg-indigo-700 disabled:opacity-70 transition-all">
            {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Eye className="w-4 h-4" />}
            Show Report
          </button>
        </div>
      </div>
    </>
  )
}

// ─── DESKTOP TABLE ROW ────────────────────────────────────────────────────────

function DesktopRow({ row, idx, onStudentClick, onDelete }) {
  const { fg, bg } = classColor(row.classname)
  return (
    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors group">
      <td className="px-4 py-3 text-center text-[12px] text-slate-400 tabular-nums w-12">{idx}</td>
      <td className="px-4 py-3">
        <div className="flex items-center gap-2.5">
          <span className="w-8 h-8 rounded-lg flex-shrink-0 flex items-center justify-center text-[9px] font-bold" style={{ background: bg, color: fg }}>
            VC
          </span>
          <span className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 whitespace-nowrap">{row.v_class}</span>
        </div>
      </td>
      <td className="px-4 py-3">
        <div className="flex items-center gap-2">
          <span className="w-6 h-6 rounded-md flex items-center justify-center text-[9px] font-bold flex-shrink-0" style={{ background: bg, color: fg }}>
            {formatAbbr(row.classname)}
          </span>
          <span className="text-[12px] text-slate-600 dark:text-slate-300 whitespace-nowrap">{row.classname}</span>
        </div>
      </td>
      <td className="px-4 py-3">
        <div className="flex items-center gap-2">
          <span className="w-6 h-6 rounded-full bg-indigo-100 dark:bg-indigo-500/15 flex items-center justify-center text-[9px] font-bold text-indigo-600 dark:text-indigo-400 flex-shrink-0">
            {row.facultyname.charAt(0)}
          </span>
          <span className="text-[12px] text-slate-600 dark:text-slate-300 whitespace-nowrap">{row.facultyname}</span>
        </div>
      </td>
      <td className="px-4 py-3 text-center">
        <button
          onClick={() => onStudentClick(row)}
          className="inline-flex items-center gap-1.5 px-3 py-1 rounded-lg text-[12px] font-semibold
            bg-blue-50 text-blue-700 hover:bg-blue-100 dark:bg-blue-500/10 dark:text-blue-400 dark:hover:bg-blue-500/20
            border border-blue-200 dark:border-blue-500/25 transition-colors"
        >
          <Users className="w-3.5 h-3.5" />
          {row.studentcount}
        </button>
      </td>
      <td className="px-4 py-3">
        <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-[12px] font-medium
          bg-emerald-50 text-emerald-700 dark:bg-emerald-500/10 dark:text-emerald-400 border border-emerald-100 dark:border-emerald-500/20">
          <BookOpen className="w-3 h-3" />
          {row.subject}
        </span>
      </td>
      <td className="px-4 py-3 text-center">
        <button
          onClick={() => onDelete(row)}
          className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold
            bg-rose-50 text-rose-600 hover:bg-rose-100 dark:bg-rose-500/10 dark:text-rose-400 dark:hover:bg-rose-500/20
            border border-rose-100 dark:border-rose-500/20 transition-all active:scale-95"
        >
          <Trash2 className="w-3.5 h-3.5" />
          Delete
        </button>
      </td>
    </tr>
  )
}

// ─── MOBILE CARD ──────────────────────────────────────────────────────────────

function MobileCard({ row, idx, onStudentClick, onDelete }) {
  const [expanded, setExpanded] = useState(false)
  const { fg, bg } = classColor(row.classname)
  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
      {/* Header */}
      <button type="button" onClick={() => setExpanded(p => !p)}
        className="w-full flex items-center gap-3 px-4 py-3.5 text-left hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors">
        <span className="w-9 h-9 rounded-xl flex-shrink-0 flex items-center justify-center text-[10px] font-bold" style={{ background: bg, color: fg }}>
          VC
        </span>
        <div className="flex-1 min-w-0">
          <p className="text-[13px] font-bold text-slate-800 dark:text-slate-100 leading-tight truncate">{row.v_class}</p>
          <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5 truncate">{row.classname} · {row.facultyname}</p>
        </div>
        <div className="flex items-center gap-2 flex-shrink-0">
          <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-700 dark:bg-emerald-500/15 dark:text-emerald-400">
            {row.subject}
          </span>
          <ChevronRight className={`w-4 h-4 text-slate-400 transition-transform ${expanded ? 'rotate-90' : ''}`} />
        </div>
      </button>

      {/* Quick stats row */}
      <div className="flex items-center gap-3 px-4 pb-3">
        <button
          onClick={() => onStudentClick(row)}
          className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold
            bg-blue-50 text-blue-700 dark:bg-blue-500/10 dark:text-blue-400
            border border-blue-200 dark:border-blue-500/25 hover:bg-blue-100 transition-colors"
        >
          <Users className="w-3.5 h-3.5" />
          {row.studentcount} Students
        </button>
        <span className="text-[11px] text-slate-400 dark:text-slate-500 tabular-nums">#{idx}</span>
      </div>

      {/* Expanded detail */}
      {expanded && (
        <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] px-4 py-4 space-y-3">
          <div className="grid grid-cols-2 gap-2.5">
            <div className="rounded-xl border border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50 dark:bg-[#1e2238] p-3">
              <p className="text-[10px] font-bold uppercase tracking-wide text-slate-400 dark:text-slate-500 mb-1">Virtual Class ID</p>
              <p className="text-[12px] font-semibold text-slate-700 dark:text-slate-300 font-mono">{row.VClass_id}</p>
            </div>
            <div className="rounded-xl border border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50 dark:bg-[#1e2238] p-3">
              <p className="text-[10px] font-bold uppercase tracking-wide text-slate-400 dark:text-slate-500 mb-1">Student Count</p>
              <p className="text-[16px] font-bold text-blue-700 dark:text-blue-400 tabular-nums">{row.studentcount}</p>
            </div>
          </div>
          <div className="rounded-xl border border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50 dark:bg-[#1e2238] p-3">
            <p className="text-[10px] font-bold uppercase tracking-wide text-slate-400 dark:text-slate-500 mb-1">Faculty</p>
            <div className="flex items-center gap-2">
              <span className="w-7 h-7 rounded-full bg-indigo-100 dark:bg-indigo-500/15 flex items-center justify-center text-[10px] font-bold text-indigo-600 dark:text-indigo-400 flex-shrink-0">
                {row.facultyname.charAt(0)}
              </span>
              <p className="text-[13px] font-semibold text-slate-700 dark:text-slate-300">{row.facultyname}</p>
            </div>
          </div>
          <button
            onClick={() => onDelete(row)}
            className="w-full flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
              bg-rose-50 text-rose-600 hover:bg-rose-100 dark:bg-rose-500/10 dark:text-rose-400
              border border-rose-200 dark:border-rose-500/25 transition-colors active:scale-[.98]"
          >
            <Trash2 className="w-4 h-4" />
            Delete Virtual Class
          </button>
        </div>
      )}
    </div>
  )
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────────

export default function VirtualClassReport() {
  // Filter state
  const [filters, setFilters] = useState({ session: '', cls: '', section: '', subject: '', faculty: '' })
  const [errors,  setErrors]  = useState({})

  // Data state
  const [rows,    setRows]    = useState([])
  const [loading, setLoading] = useState(false)
  const [exporting, setExporting] = useState(false)
  const [shown,   setShown]   = useState(false)
  const [search,  setSearch]  = useState('')

  // UI state
  const [filterOpen, setFilterOpen] = useState(false)
  const [toast,      setToast]      = useState(null)

  // Delete modal
  const [deleteModal, setDeleteModal] = useState({ open: false, row: null })

  // Student list modal
  const [studentModal, setStudentModal] = useState({ open: false, row: null })

  // ── Derived cascade data ──────────────────────────────────────────────────
  const derivedData = useMemo(() => ({
    classes:  filters.session ? (CLASSES_DATA[filters.session] || []) : [],
    sections: filters.cls ? (SECTIONS_DATA[filters.cls] || []) : [],
    subjects: filters.cls ? (SUBJECTS_DATA[filters.cls] || []) : [],
  }), [filters.session, filters.cls])

  const showToast = useCallback((msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }, [])

  // ── Submit ────────────────────────────────────────────────────────────────
  const handleSubmit = useCallback(() => {
    const err = {}
    if (!filters.session) err.session = 'Please select a session'
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setLoading(true)
    setSearch('')

    // Simulate API call — in production replace with fetch()
    setTimeout(() => {
      let data = [...VIRTUAL_CLASSES_DATA]
      if (filters.cls)     data = data.filter(r => r.classname.startsWith(filters.cls))
      if (filters.subject) data = data.filter(r => r.subject === filters.subject)
      if (filters.faculty) data = data.filter(r => r.facultyname === filters.faculty)
      setRows(data)
      setShown(true)
      setLoading(false)
      showToast(`Loaded ${data.length} virtual class${data.length !== 1 ? 'es' : ''}.`)
    }, 700)
  }, [filters, showToast])

  // ── Excel Export ──────────────────────────────────────────────────────────
  const handleExcel = useCallback(() => {
    if (rows.length === 0) { showToast('No data to export. Show report first.', 'error'); return }
    setExporting(true)
    setTimeout(() => {
      setExporting(false)
      showToast('Excel export ready! (API integration pending)')
    }, 1000)
  }, [rows.length, showToast])

  // ── Reset ─────────────────────────────────────────────────────────────────
  const handleReset = () => {
    setFilters({ session: '', cls: '', section: '', subject: '', faculty: '' })
    setErrors({}); setRows([]); setSearch(''); setShown(false)
  }

  // ── Delete flow ───────────────────────────────────────────────────────────
  const handleDeleteClick  = (row) => setDeleteModal({ open: true, row })
  const handleDeleteCancel = () => setDeleteModal({ open: false, row: null })
  const handleDeleteConfirm = () => {
    const name = deleteModal.row?.v_class
    setRows(prev => prev.filter(r => r.VClass_id !== deleteModal.row?.VClass_id))
    setDeleteModal({ open: false, row: null })
    showToast(`"${name}" deleted successfully.`)
  }

  // ── Student list modal ────────────────────────────────────────────────────
  const handleStudentClick = (row) => setStudentModal({ open: true, row })
  const handleStudentClose = () => setStudentModal({ open: false, row: null })

  // ── Search filter ─────────────────────────────────────────────────────────
  const filtered = useMemo(() => {
    if (!search) return rows
    const q = search.toLowerCase()
    return rows.filter(r =>
      r.v_class.toLowerCase().includes(q) ||
      r.classname.toLowerCase().includes(q) ||
      r.facultyname.toLowerCase().includes(q) ||
      r.subject.toLowerCase().includes(q)
    )
  }, [rows, search])

  const hasResults  = shown && rows.length > 0
  const activeCount = Object.values(filters).filter(Boolean).length

  // ── Student list for modal ────────────────────────────────────────────────
  const modalStudents = useMemo(() => {
    if (!studentModal.row) return []
    return STUDENTS_BY_VCLASS[studentModal.row.VClass_id] || []
  }, [studentModal.row])

  return (
    <div className="space-y-4 pb-10">

      {/* Page Title */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <GraduationCap className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            Virtual Class Report
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            View, manage and delete virtual classes — faculty, subject &amp; student mapping.
          </p>
        </div>
        {hasResults && (
          <button type="button" onClick={handleExcel} disabled={exporting}
            className="hidden sm:flex items-center gap-2 px-4 py-2 rounded-xl text-[13px] font-semibold
              bg-emerald-600 text-white hover:bg-emerald-700 shadow-md shadow-emerald-500/20
              transition-all active:scale-95 disabled:opacity-70 flex-shrink-0">
            {exporting ? <Loader2 className="w-4 h-4 animate-spin" /> : <FileSpreadsheet className="w-4 h-4" />}
            Export Excel
          </button>
        )}
      </div>

      {/* ── DESKTOP FILTER CARD ──────────────────────────────────────────── */}
      <div className="hidden sm:block rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <Filter className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Search Filters</span>
        </div>
        <div className="p-5">
          <div className="grid grid-cols-2 lg:grid-cols-5 gap-4 items-end">
            {/* Session */}
            <Field label="Session" error={errors.session} required>
              <NativeSelect
                value={filters.session}
                onChange={e => {
                  setFilters({ session: e.target.value, cls: '', section: '', subject: '', faculty: '' })
                  setErrors(p => ({ ...p, session: undefined }))
                }}
                placeholder="-- Select Session --"
                error={errors.session}
              >
                {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
              </NativeSelect>
            </Field>

            {/* Class */}
            <Field label="Class">
              <NativeSelect
                value={filters.cls}
                onChange={e => setFilters(p => ({ ...p, cls: e.target.value, section: '', subject: '' }))}
                placeholder="-- Select Class --"
                disabled={!filters.session}
              >
                {derivedData.classes.map(c => <option key={c} value={c}>{c}</option>)}
              </NativeSelect>
            </Field>

            {/* Section */}
            <Field label="Section">
              <NativeSelect
                value={filters.section}
                onChange={e => setFilters(p => ({ ...p, section: e.target.value }))}
                placeholder="-- Select Section --"
                disabled={!filters.cls}
              >
                {derivedData.sections.map(s => <option key={s} value={s}>{s}</option>)}
              </NativeSelect>
            </Field>

            {/* Subject */}
            <Field label="Subject">
              <NativeSelect
                value={filters.subject}
                onChange={e => setFilters(p => ({ ...p, subject: e.target.value }))}
                placeholder="-- Select Subject --"
                disabled={!filters.cls}
              >
                {derivedData.subjects.map(s => <option key={s} value={s}>{s}</option>)}
              </NativeSelect>
            </Field>

            {/* Faculty */}
            <Field label="Faculty">
              <NativeSelect
                value={filters.faculty}
                onChange={e => setFilters(p => ({ ...p, faculty: e.target.value }))}
                placeholder="-- Select Faculty --"
              >
                {FACULTY_DATA.map(f => <option key={f} value={f}>{f}</option>)}
              </NativeSelect>
            </Field>
          </div>

          <div className="flex items-center justify-end gap-2 mt-4 pt-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
            <button type="button" onClick={handleReset}
              className="flex items-center gap-2 px-4 py-2 rounded-xl text-[13px] font-semibold
                bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors">
              <RefreshCw className="w-3.5 h-3.5" />
              Reset
            </button>
            <button type="button" onClick={handleSubmit} disabled={loading}
              className="flex items-center gap-2 px-6 py-2 rounded-xl text-[13px] font-semibold text-white
                bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20 dark:bg-indigo-600 dark:hover:bg-indigo-700
                transition-all active:scale-95 disabled:opacity-70">
              {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Eye className="w-4 h-4" />}
              Submit
            </button>
          </div>
        </div>
      </div>

      {/* ── MOBILE FILTER BAR ────────────────────────────────────────────── */}
      <div className="flex sm:hidden gap-2">
        <button type="button" onClick={() => setFilterOpen(true)}
          className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold bg-blue-600 text-white dark:bg-indigo-600 shadow-md shadow-blue-500/20">
          <SlidersHorizontal className="w-4 h-4" />
          {activeCount > 0 ? `Filters (${activeCount})` : 'Select Filters'}
        </button>
        {hasResults && (
          <button type="button" onClick={handleExcel} disabled={exporting}
            className="flex items-center gap-1.5 px-4 py-2.5 rounded-xl text-[13px] font-semibold bg-emerald-600 text-white disabled:opacity-70">
            {exporting ? <Loader2 className="w-4 h-4 animate-spin" /> : <FileSpreadsheet className="w-4 h-4" />}
          </button>
        )}
        <button type="button" onClick={handleReset}
          className="px-3.5 py-2.5 rounded-xl bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300">
          <RefreshCw className="w-4 h-4" />
        </button>
      </div>

      <FilterDrawer
        open={filterOpen}
        onClose={() => setFilterOpen(false)}
        filters={filters}
        setFilters={setFilters}
        derivedData={derivedData}
        onSubmit={handleSubmit}
        loading={loading}
        errors={errors}
      />

      {/* ── LOADING SKELETON ─────────────────────────────────────────────── */}
      {loading && (
        <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] p-6 space-y-3">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="h-12 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" style={{ opacity: 1 - i * 0.15 }} />
          ))}
        </div>
      )}

      {/* ── RESULTS TABLE ────────────────────────────────────────────────── */}
      {hasResults && !loading && (
        <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

          {/* Card Header */}
          <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
            <div className="flex items-center gap-3 flex-1 min-w-0 flex-wrap">
              <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
              <LayoutList className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
              <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Virtual Class List</span>
              {filters.session && (
                <span className="text-[13px] text-slate-400 dark:text-slate-500">· {filters.session}</span>
              )}
              <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400 flex-shrink-0">
                {filtered.length} record{filtered.length !== 1 ? 's' : ''}
              </span>
            </div>
            {/* Search */}
            <div className="relative w-full sm:w-52 flex-shrink-0">
              <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
              <input
                value={search}
                onChange={e => setSearch(e.target.value)}
                placeholder="Search class, faculty…"
                className="w-full pl-8 pr-7 py-1.5 text-[12px] rounded-lg border outline-none transition-all
                  bg-white text-slate-700 border-slate-200 placeholder-slate-300
                  focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                  dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]
                  dark:placeholder-slate-600 dark:focus:border-indigo-400"
              />
              {search && (
                <button onClick={() => setSearch('')} className="absolute right-2 top-1/2 -translate-y-1/2 text-slate-400">
                  <X className="w-3.5 h-3.5" />
                </button>
              )}
            </div>
          </div>

          {/* Info hint */}
          <div className="hidden sm:flex items-center gap-2 px-5 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] bg-blue-50/20 dark:bg-blue-500/[0.03]">
            <Info className="w-3.5 h-3.5 text-blue-500 flex-shrink-0" />
            <p className="text-[12px] text-blue-700 dark:text-blue-400">
              Click the student count badge to view enrolled students. Click Delete to remove a virtual class.
            </p>
          </div>

          {/* ── DESKTOP TABLE ── */}
          <div className="hidden md:block overflow-x-auto">
            {filtered.length === 0 ? (
              <div className="flex flex-col items-center justify-center py-16 gap-2 text-slate-400">
                <Search className="w-6 h-6 opacity-40" />
                <span className="text-[13px]">No records match your search.</span>
              </div>
            ) : (
              <table className="w-full">
                <thead>
                  <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                    {['S.No.', 'Virtual Class', 'Class', 'Faculty Name', 'Student Count', 'Subject', 'Action'].map((h, i) => (
                      <th key={i} className="px-4 py-2.5 text-left text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap first:text-center first:w-12">
                        {h}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {filtered.map((row, i) => (
                    <DesktopRow
                      key={row.VClass_id}
                      row={row}
                      idx={i + 1}
                      onStudentClick={handleStudentClick}
                      onDelete={handleDeleteClick}
                    />
                  ))}
                </tbody>
              </table>
            )}
          </div>

          {/* ── MOBILE CARDS ── */}
          <div className="md:hidden p-3 space-y-3">
            {filtered.length === 0 ? (
              <div className="flex flex-col items-center justify-center py-12 gap-2 text-slate-400">
                <Search className="w-6 h-6 opacity-40" />
                <span className="text-[13px]">No records match your search.</span>
              </div>
            ) : (
              <>
                <p className="text-[11px] text-blue-600 dark:text-blue-400 font-medium flex items-center gap-1.5 pb-1">
                  <Info className="w-3.5 h-3.5 flex-shrink-0" />
                  Tap a card to expand details &amp; delete.
                </p>
                {filtered.map((row, i) => (
                  <MobileCard
                    key={row.VClass_id}
                    row={row}
                    idx={i + 1}
                    onStudentClick={handleStudentClick}
                    onDelete={handleDeleteClick}
                  />
                ))}
              </>
            )}
          </div>

          {/* Table Footer */}
          <div className="flex items-center justify-between gap-3 px-5 py-3.5 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
            <p className="text-[12px] text-slate-400 dark:text-slate-500">
              Showing <span className="font-semibold text-slate-700 dark:text-slate-300">{filtered.length}</span> of{' '}
              <span className="font-semibold text-slate-700 dark:text-slate-300">{rows.length}</span> records
            </p>
            {search && (
              <button onClick={() => setSearch('')}
                className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1">
                <X className="w-3 h-3" /> Clear search
              </button>
            )}
          </div>
        </div>
      )}

      {/* ── EMPTY STATE ───────────────────────────────────────────────────── */}
      {!hasResults && !loading && (
        <div className="flex flex-col items-center justify-center py-16 gap-4 text-slate-400 dark:text-slate-600">
          <div className="w-14 h-14 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
            <GraduationCap className="w-7 h-7 opacity-50" />
          </div>
          <div className="text-center">
            <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">No report generated yet</p>
            <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
              Select a session and click <strong>Submit</strong> to generate the virtual class report.
            </p>
          </div>
        </div>
      )}

      {/* ── MODALS ────────────────────────────────────────────────────────── */}
      <DeleteModal
        open={deleteModal.open}
        vcName={deleteModal.row?.v_class}
        onConfirm={handleDeleteConfirm}
        onCancel={handleDeleteCancel}
      />

      <StudentListModal
        open={studentModal.open}
        onClose={handleStudentClose}
        students={modalStudents}
        vcName={studentModal.row?.v_class}
      />

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
