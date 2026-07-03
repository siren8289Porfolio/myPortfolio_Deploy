import React, { useState } from 'react';
import { Page } from '@/shared/types/appTypes';
import { motion } from 'motion/react';
import { toast } from 'sonner';
import { ShieldCheck } from 'lucide-react';

interface LoginProps {
  onNavigate: (page: Page) => void;
}

export function Login({ onNavigate }: LoginProps) {
  const [id, setId] = useState('admin');
  const [password, setPassword] = useState('1234');
  const [error, setError] = useState('');

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    // Default admin check
    if (id === 'admin' && password === '1234') {
      toast.success('관리자 모드로 로그인했습니다.');
      onNavigate('dashboard');
      return;
    }

    // Check localStorage users
    const users = JSON.parse(localStorage.getItem('if_users') || '[]');
    const validUser = users.find((u: any) => u.id === id && u.password === password);

    if (validUser) {
      toast.success(`${validUser.name}님 환영합니다.`);
      onNavigate('dashboard');
    } else {
      setError('아이디 또는 비밀번호가 올바르지 않습니다.');
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="bg-white w-full max-w-5xl rounded-2xl shadow-2xl overflow-hidden flex h-auto md:h-[650px]"
      >
        {/* Left Side - Branding (No Image) */}
        <div className="hidden md:flex w-1/2 relative bg-gradient-to-br from-[#2F8F6B] to-[#1e6b4e] items-center justify-center overflow-hidden">
          {/* Decorative Circles */}
          <div className="absolute top-[-20%] left-[-20%] w-[80%] h-[80%] rounded-full border-[2px] border-white/10" />
          <div className="absolute bottom-[-10%] right-[-10%] w-[60%] h-[60%] rounded-full bg-white/5" />

          <div className="relative z-20 text-white p-12">
            <div className="w-20 h-20 bg-white/20 backdrop-blur-sm rounded-2xl mb-8 flex items-center justify-center border border-white/30 shadow-lg">
              <span className="text-white text-4xl font-bold">If</span>
            </div>
            <h2 className="text-4xl font-bold mb-6 leading-tight">고령자 위험도<br/>판단 시스템</h2>
            <p className="text-white/90 text-lg leading-relaxed flex flex-col gap-1">
              <span>데이터 기반의 정확한 분석으로</span>
              <span>어르신들의 안전을 지킵니다.</span>
            </p>

            <div className="mt-12 flex gap-4">
              <div className="flex items-center gap-2 bg-white/10 px-4 py-2 rounded-lg backdrop-blur-sm">
                <ShieldCheck size={20} />
                <span className="text-sm font-medium">안전성 평가</span>
              </div>
              <div className="flex items-center gap-2 bg-white/10 px-4 py-2 rounded-lg backdrop-blur-sm">
                <div className="w-5 h-5 flex items-center justify-center font-bold border border-white rounded-full text-[10px]">AI</div>
                <span className="text-sm font-medium">정밀 분석</span>
              </div>
            </div>
          </div>
        </div>

        {/* Right Side - Login Form */}
        <div className="w-full md:w-1/2 p-8 md:p-16 flex flex-col justify-center">
          <div className="max-w-md mx-auto w-full">
            <div className="mb-10">
              <h1 className="text-3xl font-bold text-gray-800 mb-2">로그인</h1>
              <p className="text-gray-500">관리자 계정으로 접속해주세요.</p>
            </div>

            <form onSubmit={handleLogin} className="space-y-6">
              <div>
                <label className="block text-sm font-bold text-gray-700 mb-2">아이디</label>
                <input
                  type="text"
                  value={id}
                  onChange={(e) => setId(e.target.value)}
                  className="w-full px-5 py-4 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#2F8F6B] focus:border-transparent transition-all"
                  placeholder="아이디를 입력하세요"
                />
              </div>
              <div>
                <div className="flex justify-between items-center mb-2">
                  <label className="block text-sm font-bold text-gray-700">비밀번호</label>
                </div>
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full px-5 py-4 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#2F8F6B] focus:border-transparent transition-all"
                  placeholder="비밀번호를 입력하세요"
                />
              </div>

              {error && (
                <div className="p-3 bg-red-50 text-red-500 text-sm rounded-lg flex items-center gap-2">
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                  {error}
                </div>
              )}

              <button
                type="submit"
                className="w-full bg-[#2F8F6B] hover:bg-[#257A5A] text-white font-bold py-4 rounded-xl shadow-lg shadow-[#2F8F6B]/30 transition-all transform active:scale-[0.99]"
              >
                로그인
              </button>
            </form>

            <div className="mt-8 text-center border-t border-gray-100 pt-6">
              <p className="text-gray-500">
                아직 계정이 없으신가요?{' '}
                <button
                  onClick={() => onNavigate('signup')}
                  className="text-[#2F8F6B] font-bold hover:underline ml-1"
                >
                  회원가입
                </button>
              </p>
            </div>
          </div>
        </div>
      </motion.div>
    </div>
  );
}
